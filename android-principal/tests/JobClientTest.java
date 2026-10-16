package com.impresionrapida;
import java.io.*;
public class JobClientTest {
 public static void main(String[] args)throws Exception{
  Ipp i=new Ipp("127.0.0.1","/ipp/print",Integer.parseInt(args[0]));
  if(!i.capabilities().has("document-format-supported","application/pdf"))throw new AssertionError();
  File file=new File(args[1]);
  int id=i.createJob(1,"two-sided-short-edge","monochrome","application/pdf").integer("job-id",-1);if(id!=41)throw new AssertionError();
  i.sendDocument(file,id,"application/pdf");
  if(i.job(id).integer("job-state",0)!=5)throw new AssertionError("Should be printing");
  i.cancel(id);
  if(i.job(id).integer("job-state",0)!=7)throw new AssertionError("Should be canceled");
  int second=i.print(file,1,"one-sided","color","application/pdf").integer("job-id",0);if(second!=42)throw new AssertionError();
  if(i.job(second).integer("job-state",0)!=9)throw new AssertionError("Should be complete");
  System.out.println("PASS: real HTTP Create-Job, Send-Document, processing, Cancel-Job, canceled, Print-Job fallback and completed.");
 }
}
