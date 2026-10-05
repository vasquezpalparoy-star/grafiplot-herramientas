export const PRESETS={a3:{columns:2,rows:1,label:'A3'},a2:{columns:2,rows:2,label:'A2'},a1:{columns:4,rows:2,label:'A1'},a0:{columns:4,rows:4,label:'A0'}};
export const MM_TO_PT=72/25.4;
export function posterGeometry(columns,rows,landscape=false,margin=5){const paperW=landscape?297:210,paperH=landscape?210:297;const tileW=paperW-2*margin,tileH=paperH-2*margin;return {columns,rows,margin,paperW,paperH,tileW,tileH,width:columns*tileW,height:rows*tileH,count:columns*rows}}
export function placeSource(sw,sh,pw,ph,mode='contain'){if(mode==='stretch')return {x:0,y:0,width:pw,height:ph};const scale=mode==='cover'?Math.max(pw/sw,ph/sh):Math.min(pw/sw,ph/sh);const width=sw*scale,height=sh*scale;return {x:(pw-width)/2,y:(ph-height)/2,width,height}}
