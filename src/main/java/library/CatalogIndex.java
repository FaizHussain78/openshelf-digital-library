package library;

import java.util.*;

/** Maintains ordinary BST and self-balancing AVL ISBN indexes. */
final class CatalogIndex {
    private Node bst,avl;
    private static final class Node { Book b; Node l,r; int h=1; Node(Book b){this.b=b;} }
    void rebuild(Collection<Book> books){bst=null;avl=null;for(Book b:books){bst=put(bst,b,false);avl=put(avl,b,true);}}
    Book find(String isbn){Node n=avl;while(n!=null){int c=isbn.compareTo(n.b.isbn());if(c==0)return n.b;n=c<0?n.l:n.r;}return null;}
    int avlHeight(){return height(avl);} int bstHeight(){return height(bst);}
    private int height(Node n){return n==null?0:n.h;}
    private Node put(Node n,Book b,boolean balance){if(n==null)return new Node(b);int c=b.isbn().compareTo(n.b.isbn());if(c<0)n.l=put(n.l,b,balance);else if(c>0)n.r=put(n.r,b,balance);else n.b=b;
        n.h=1+Math.max(height(n.l),height(n.r)); if(!balance)return n;int bf=height(n.l)-height(n.r);
        if(bf>1&&b.isbn().compareTo(n.l.b.isbn())<0)return right(n); if(bf>1){n.l=left(n.l);return right(n);} if(bf< -1&&b.isbn().compareTo(n.r.b.isbn())>0)return left(n);if(bf< -1){n.r=right(n.r);return left(n);}return n;}
    private Node right(Node y){Node x=y.l,t=x.r;x.r=y;y.l=t;y.h=1+Math.max(height(y.l),height(y.r));x.h=1+Math.max(height(x.l),height(x.r));return x;}
    private Node left(Node x){Node y=x.r,t=y.l;y.l=x;x.r=t;x.h=1+Math.max(height(x.l),height(x.r));y.h=1+Math.max(height(y.l),height(y.r));return y;}
}
