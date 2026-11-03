package library;
import java.util.*;

/** Weighted graph links books sharing a genre or author. */
final class BookGraph {
    record Edge(String to,int weight){} record Link(String from,String to,int weight){}
    private final Map<String,List<Edge>> adj=new LinkedHashMap<>();
    BookGraph(List<Book> books){for(Book b:books)adj.put(b.isbn(),new ArrayList<>());for(int i=0;i<books.size();i++)for(int j=i+1;j<books.size();j++){Book a=books.get(i),b=books.get(j);int w=a.author().equalsIgnoreCase(b.author())?1:a.genre().equalsIgnoreCase(b.genre())?2:0;if(w>0){adj.get(a.isbn()).add(new Edge(b.isbn(),w));adj.get(b.isbn()).add(new Edge(a.isbn(),w));}}}
    List<String> bfs(String start){return walk(start,false);} List<String> dfs(String start){return walk(start,true);}
    private List<String> walk(String s,boolean depth){List<String> out=new ArrayList<>();if(!adj.containsKey(s))return out;Set<String> seen=new LinkedHashSet<>();Deque<String> q=new ArrayDeque<>();q.add(s);while(!q.isEmpty()){String u=depth?q.removeLast():q.removeFirst();if(!seen.add(u))continue;out.add(u);for(Edge e:adj.get(u))if(!seen.contains(e.to())){if(depth)q.addLast(e.to());else q.addLast(e.to());}}return out;}
    List<Link> shortest(String start){Map<String,Integer>d=new HashMap<>();PriorityQueue<String[]>q=new PriorityQueue<>(Comparator.comparingInt(a->Integer.parseInt(a[1])));for(String v:adj.keySet())d.put(v,Integer.MAX_VALUE);if(!d.containsKey(start))return List.of();d.put(start,0);q.add(new String[]{start,"0"});while(!q.isEmpty()){String[] x=q.poll();String u=x[0];int du=Integer.parseInt(x[1]);if(du!=d.get(u))continue;for(Edge e:adj.get(u))if(du+e.weight()<d.get(e.to())){d.put(e.to(),du+e.weight());q.add(new String[]{e.to(),""+(du+e.weight())});}}List<Link>out=new ArrayList<>();d.forEach((v,w)->out.add(new Link(start,v,w)));return out;}
    List<Link> prim(){if(adj.isEmpty())return List.of();List<Link> out=new ArrayList<>();Set<String> seen=new HashSet<>();PriorityQueue<Link>q=new PriorityQueue<>(Comparator.comparingInt(Link::weight));for(String root:adj.keySet())if(seen.add(root)){for(Edge e:adj.get(root))q.add(new Link(root,e.to(),e.weight()));while(!q.isEmpty()){Link e=q.poll();if(seen.contains(e.to()))continue;seen.add(e.to());out.add(e);for(Edge x:adj.get(e.to()))if(!seen.contains(x.to()))q.add(new Link(e.to(),x.to(),x.weight()));}}return out;}
}
