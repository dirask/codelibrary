package graphs.flows;

import java.util.*;
import java.util.stream.Stream;

public class MaxFlowOfMinCostDense {

    static class Edge {
        int to, f, cap, cost, rev;

        Edge(int to, int cap, int cost, int rev) {
            this.to = to;
            this.cap = cap;
            this.cost = cost;
            this.rev = rev;
        }
    }

    public static void addEdge(List<Edge>[] graph, int s, int t, int cap, int cost) {
        graph[s].add(new Edge(t, cap, cost, graph[t].size()));
        graph[t].add(new Edge(s, 0, -cost, graph[s].size() - 1));
    }

    static void bellmanFord(List<Edge>[] graph, int s, int[] dist) {
        int n = graph.length;
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[s] = 0;
        boolean[] inqueue = new boolean[n];
        int[] q = new int[n];
        int qt = 0;
        q[qt++] = s;
        for (int qh = 0; qh != qt; qh++) {
            int u = q[qh % n];
            inqueue[u] = false;
            for (int i = 0; i < graph[u].size(); i++) {
                Edge e = graph[u].get(i);
                if (e.cap <= e.f)
                    continue;
                int v = e.to;
                int ndist = dist[u] + e.cost;
                if (dist[v] > ndist) {
                    dist[v] = ndist;
                    if (!inqueue[v]) {
                        inqueue[v] = true;
                        q[qt++ % n] = v;
                    }
                }
            }
        }
    }

    public static int[] minCostFlow(List<Edge>[] graph, int s, int t, int maxf) {
        int n = graph.length;
        int[] prio = new int[n];
        int[] curflow = new int[n];
        int[] prevedge = new int[n];
        int[] prevnode = new int[n];
        int[] pot = new int[n];

        // bellmanFord invocation can be skipped if edges costs are non-negative
        bellmanFord(graph, s, pot);
        int flow = 0;
        int flowCost = 0;
        while (flow < maxf) {
            Arrays.fill(prio, Integer.MAX_VALUE);
            prio[s] = 0;
            boolean[] finished = new boolean[n];
            curflow[s] = Integer.MAX_VALUE;
            for (int i = 0; i < n && !finished[t]; i++) {
                int u = -1;
                for (int j = 0; j < n; j++)
                    if (!finished[j] && (u == -1 || prio[u] > prio[j]))
                        u = j;
                if (prio[u] == Integer.MAX_VALUE)
                    break;
                finished[u] = true;
                for (int k = 0; k < graph[u].size(); k++) {
                    Edge e = graph[u].get(k);
                    if (e.f >= e.cap)
                        continue;
                    int v = e.to;
                    int nprio = prio[u] + e.cost + pot[u] - pot[v];
                    if (prio[v] > nprio) {
                        prio[v] = nprio;
                        prevnode[v] = u;
                        prevedge[v] = k;
                        curflow[v] = Math.min(curflow[u], e.cap - e.f);
                    }
                }
            }
            if (prio[t] == Integer.MAX_VALUE)
                break;
            for (int i = 0; i < n; i++)
                if (finished[i])
                    pot[i] += prio[i] - prio[t];
            int df = Math.min(curflow[t], maxf - flow);
            flow += df;
            for (int v = t; v != s; v = prevnode[v]) {
                Edge e = graph[prevnode[v]].get(prevedge[v]);
                e.f += df;
                graph[v].get(e.rev).f -= df;
                flowCost += df * e.cost;
            }
        }
        return new int[]{flow, flowCost};
    }

    // Usage example
    public static void main(String[] args) {
        List<Edge>[] graph = Stream.generate(ArrayList::new).limit(3).toArray(List[]::new);
        addEdge(graph, 0, 1, 3, 1);
        addEdge(graph, 0, 2, 2, 1);
        addEdge(graph, 1, 2, 2, 1);
        int[] res = minCostFlow(graph, 0, 2, Integer.MAX_VALUE);
        int flow = res[0];
        int flowCost = res[1];
        System.out.println(4 == flow);
        System.out.println(6 == flowCost);
    }
}
