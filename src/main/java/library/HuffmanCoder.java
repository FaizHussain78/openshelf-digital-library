package library;
import java.util.*;

/** Huffman coding demonstration used to estimate description compression. */
final class HuffmanCoder {
    private static final class N {char c;int f;N l,r;N(char c,int f){this.c=c;this.f=f;}N(N l,N r){this.l=l;this.r=r;f=l.f+r.f;}}
    static Map<String,Object> measure(String s){Map<String,Object> result=new LinkedHashMap<>();if(s.isEmpty()){result.put("originalBits",0);result.put("encodedBits",0);result.put("codes",Map.of());return result;}Map<Character,Integer>freq=new HashMap<>();for(char c:s.toCharArray())freq.merge(c,1,Integer::sum);PriorityQueue<N>q=new PriorityQueue<>(Comparator.comparingInt(n->n.f));freq.forEach((c,f)->q.add(new N(c,f)));while(q.size()>1)q.add(new N(q.poll(),q.poll()));Map<Character,String>codes=new TreeMap<>();code(q.poll(),"",codes);int bits=0;for(char c:s.toCharArray())bits+=codes.get(c).length();Map<String,String> printable=new LinkedHashMap<>();codes.forEach((c,v)->printable.put(c==' '?"space":Character.toString(c),v));result.put("originalBits",s.length()*8);result.put("encodedBits",bits);result.put("codes",printable);return result;}
    private static void code(N n,String p,Map<Character,String>m){if(n.l==null&&n.r==null){m.put(n.c,p.isEmpty()?"0":p);return;}code(n.l,p+"0",m);code(n.r,p+"1",m);}
}
