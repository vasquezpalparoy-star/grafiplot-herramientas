"""Loopback IPP integration test; no real printer or network device contacted."""
from http.server import BaseHTTPRequestHandler, HTTPServer
import threading, struct, subprocess, os, sys

def attr(tag,name,value):
    n=name.encode();return bytes([tag])+struct.pack('>H',len(n))+n+struct.pack('>H',len(value))+value

def number(v):return struct.pack('>i',v)
def parse(data):
    op=struct.unpack('>H',data[2:4])[0];p=8;values={};name=''
    while p<len(data):
        tag=data[p];p+=1
        if tag==3:return op,values,data[p:]
        if tag<16:continue
        n=struct.unpack('>H',data[p:p+2])[0];p+=2
        if n:name=data[p:p+n].decode()
        p+=n;size=struct.unpack('>H',data[p:p+2])[0];p+=2
        values[name]=data[p:p+size];p+=size
    raise AssertionError('Missing delimiter')

class Mock(BaseHTTPRequestHandler):
    state=3;seen=[];failures=[]
    def log_message(self,*args):pass
    def do_POST(self):
        try:
            op,v,payload=parse(self.rfile.read(int(self.headers['Content-Length'])))
            assert self.headers['Content-Type']=='application/ipp'
            assert v['attributes-charset']==b'utf-8'
            Mock.seen.append(op)
            fields=b''
            if op==11:fields=attr(0x49,'document-format-supported',b'application/pdf')
            elif op==5:
                assert v['sides']==b'two-sided-short-edge'
                assert v['copies']==number(1)
                assert payload==b''
                fields=attr(0x21,'job-id',number(41))
            elif op==6:
                assert v['job-id']==number(41) and v['last-document']==b'\1'
                assert payload==b'%PDF-test'
                Mock.state=5
            elif op==8:
                assert v['job-id']==number(41)
                Mock.state=7
            elif op==9:
                ident=struct.unpack('>i',v['job-id'])[0]
                fields=attr(0x23,'job-state',number(9 if ident==42 else Mock.state))
            elif op==2:
                assert v['sides']==b'one-sided' and payload==b'%PDF-test'
                fields=attr(0x21,'job-id',number(42))
            else:raise AssertionError(op)
            body=struct.pack('>HHI',0x101,0,1)+b'\2'+fields+b'\3'
            self.send_response(200);self.send_header('Content-Type','application/ipp');self.send_header('Content-Length',str(len(body)));self.end_headers();self.wfile.write(body)
        except Exception as e:
            Mock.failures.append(str(e));self.send_error(500)

if __name__=='__main__':
    server=HTTPServer(('127.0.0.1',0),Mock);threading.Thread(target=server.serve_forever,daemon=True).start()
    java=sys.argv[1];classpath=sys.argv[2];fixture=sys.argv[3]
    open(fixture,'wb').write(b'%PDF-test')
    result=subprocess.run([java,'-cp',classpath,'com.impresionrapida.JobClientTest',str(server.server_port),fixture])
    server.shutdown()
    assert result.returncode==0 and not Mock.failures,(result.returncode,Mock.failures)
    assert Mock.seen==[11,5,6,9,8,9,2,9],Mock.seen
    print('PASS: mock printer verified transmitted job attributes, short-edge booklet ticket and job IDs.')
