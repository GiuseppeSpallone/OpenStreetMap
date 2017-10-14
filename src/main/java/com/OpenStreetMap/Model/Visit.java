/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.OpenStreetMap.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

public class Visit {

   public static void removeNotStrongConnected(HashMap<Long, Node> nodes, HashSet<Arc> arc, Node rif) {

      HashSet<Long> strongConnect = new HashSet<Long>(nodes.size());
      HashSet<Node> raggiunti = new HashSet<>();
      strongConnect.add(rif.getId());
      for (Iterator<Node> it = nodes.values().iterator(); it.hasNext();) {
         Node n = it.next();
         n.setComp(0);
      }
      System.out.println("A");
      visitaFrom(rif);
      System.out.println("B");
      ArrayList<Node> del = new ArrayList<Node>();
      for (Iterator<Node> it = nodes.values().iterator(); it.hasNext();) {
         Node n = it.next();
         if (n.getComp() == 0) {
            del.add(n);
         }
      }
      long l = System.currentTimeMillis();
      int nod = nodes.size();
      int k = 0;
      Object[] nds = nodes.values().toArray();
      Random rnd = new Random();
      for (int i = 0; i < nds.length; i++) {
         int j = rnd.nextInt(nds.length);
         Object o1 = nds[i];
         nds[i] = nds[j];
         nds[j] = o1;
      }

      for (int i = 0; i < nds.length; i++) {
         k++;
         Node n = (Node) nds[i];
         if (n != rif && !strongConnect.contains(n.getId()) && !del.contains(n)) {
            for (int j = 0; j < nds.length; j++) {
               Node nn = (Node) nds[j];
               nn.setComp(0);
               if (strongConnect.contains(nn.getId())) {
                  nn.setComp(2);
               }
            }
            raggiunti.clear();
            if (!visitaFromToX(n, strongConnect, raggiunti)) {
               del.addAll(raggiunti);
            }
//                if (!visitaFromTo(n,rif)) {
//                    del.add(n);
//                }
         }
         long l1 = System.currentTimeMillis();
         if (l1 > l + (1000 * 10)) {
            l = l1;
            System.out.println(k + " of " + nod);
         }
      }

      System.out.println("Del Node Strong Connect: " + del.size());
      for (Iterator<Node> it = del.iterator(); it.hasNext();) {
         Node node = it.next();
         nodes.remove(node.getId());
         for (Iterator<Arc> it1 = node.getNd_arcs().iterator(); it1.hasNext();) {
            Arc a = it1.next();
            arc.remove(a);
         }
      }

   }

   public static Node removeUnconnected(HashMap<Long, Node> nodes, HashSet<Arc> arc) {
      Node rif = null;
      ArrayList<Node> nd = new ArrayList<>(nodes.values());
      Collections.sort(nd, new Comparator<Node>() {

         @Override
         public int compare(Node o1, Node o2) {
            return new Integer(o1.getNd_arcs().size()).compareTo(o2.getNd_arcs().size()) * -1;
         }
      });
      for (Iterator<Node> it = nd.iterator(); it.hasNext();) {
         Node n = it.next();
         n.setComp(0);
      }
      int c = 1;
      int max = 0;
      int csel = 0;
      for (Iterator<Node> it = nd.iterator(); it.hasNext();) {
         Node n = it.next();
         if (n.getComp() == 0) {
            int sz = creaComp(c, n);

            if (sz > max) {
               max = sz;
               csel = c;
               rif = n;
            }
            c++;
         }
      }
      System.out.println("removeUnconnected B");
      ArrayList<Node> del = new ArrayList<Node>(nodes.size());
      for (Iterator<Node> it = nodes.values().iterator(); it.hasNext();) {
         Node n = it.next();
         if (n.getComp() != csel) {
            del.add(n);
         }
      }
      System.out.println("Del Node Component: " + del.size());
      for (Iterator<Node> it = del.iterator(); it.hasNext();) {
         Node node = it.next();
         nodes.remove(node.getId());
         for (Iterator<Arc> it1 = node.getNd_arcs().iterator(); it1.hasNext();) {
            Arc a = it1.next();
            arc.remove(a);
         }
      }

      return rif;
   }

   private static int creaComp(int c, Node n) {
      int sz = 1;
      n.setComp(c);
      for (Iterator<Arc> it = n.getNd_arcs().iterator(); it.hasNext();) {
         Arc arc = it.next();
         if (arc.getFrom().getComp() != c) {
            sz += creaComp(c, arc.getFrom());
         }
         if (arc.getTo().getComp() != c) {
            sz += creaComp(c, arc.getTo());
         }
      }
      return sz;
   }

   private static void visitaFrom(Node n) {
      n.setComp(1);
      for (Iterator<Arc> it = n.getNd_arcs().iterator(); it.hasNext();) {
         Arc arc = it.next();
         if (arc.getFrom() == n) {
            if (arc.getTo().getComp() == 0) {
               visitaFrom(arc.getTo());
            }
         }
      }
   }

   private static boolean visitaFromTo(Node n, Node to) {
      n.setComp(1);
      for (Iterator<Arc> it = n.getNd_arcs().iterator(); it.hasNext();) {
         Arc arc = it.next();
         if (arc.getFrom() == n) {
            if (arc.getTo() == to) {
               return true;
            }
            if (arc.getTo().getComp() == 0) {
               if (visitaFromTo(arc.getTo(), to)) {
                  return true;
               }
            }
         }
      }
      return false;
   }

   private static boolean visitaFromToX(Node n, HashSet<Long> strongConnect, HashSet<Node> raggiunti) {
      n.setComp(1);
      raggiunti.add(n);
      int fmx = Integer.MIN_VALUE;
      Node nx = null;
      for (Iterator<Arc> it = n.getNd_arcs().iterator(); it.hasNext();) {
         Arc arc = it.next();
         if (arc.getFrom() == n) {
            if (arc.getTo().getComp() == 2) {
               strongConnect.add(n.getId());
               return true;
            } else 
            if (arc.getTo().getComp() == 0){
               int f = arc.getTo().getX() + arc.getTo().getY();
               if (f > fmx) {
                  fmx = f;
                  nx = arc.getTo();
               }
            }
         }
      }
      if (nx == null) {
         return false;
      }
      if (visitaFromToX(nx, strongConnect, raggiunti)) {
         strongConnect.add(n.getId());
         return true;
      }

      for (Iterator<Arc> it = n.getNd_arcs().iterator(); it.hasNext();) {
         Arc arc = it.next();
         if (arc.getFrom() == n) {
            if (arc.getTo().getComp() == 0 && arc.getTo()!=nx) {
               if (visitaFromToX(arc.getTo(), strongConnect, raggiunti)) {
                  strongConnect.add(n.getId());
                  return true;
               }
            }
         }

      }
      return false;
   }
}
