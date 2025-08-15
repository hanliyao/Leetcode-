package MST;

import java.util.*;

public class MSTConnector {

    // 并查集（Disjoint Set Union，支持路径压缩和按大小合并）
    static class DSU {
        int[] parent; // parent[i] 表示节点 i 的父节点
        int[] size;   // size[i] 表示以 i 为根的集合大小

        // 构造函数：初始化 n 个独立的集合
        DSU(int n) {
            parent = new int[n];
            size = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i; // 每个节点的父节点初始化为自己
                size[i] = 1;   // 每个集合大小初始为 1
            }
        }

        // 查找 x 所在集合的根节点（路径压缩）
        int find(int x) {
            while (x != parent[x]) {
                parent[x] = parent[parent[x]]; // 压缩路径，加快后续查询
                x = parent[x];
            }
            return x;
        }

        // 合并 a 和 b 所在的集合（按集合大小合并）
        boolean union(int a, int b) {
            int ra = find(a), rb = find(b);
            if (ra == rb) return false; // 已在同一集合，不合并
            if (size[ra] < size[rb]) {
                int t = ra; ra = rb; rb = t; // 保证 ra 的集合较大
            }
            parent[rb] = ra; // 将较小集合合并到较大集合
            size[ra] += size[rb];
            return true;
        }
    }

    /**
     * Kruskal 算法求最小生成树的总成本
     * @param n 城市数量
     * @param connections 每条连接 [城市1, 城市2, 成本]
     * @return 最小总成本；若无法连通所有城市返回 -1
     */
    public static int minimumCostKruskal(int n, int[][] connections) {
        if (n <= 1) return 0; // 没有城市或只有一个城市，不需要连接

        // 将边转成 [cost, u, v] 格式，并忽略自环
        List<int[]> edges = new ArrayList<>();
        for (int[] e : connections) {
            int x = e[0], y = e[1], c = e[2];
            if (x == y) continue; // 忽略自环
            edges.add(new int[]{c, x - 1, y - 1}); // 转换为 0-based 索引
        }

        // 按照成本升序排序
        edges.sort(Comparator.comparingInt(a -> a[0]));

        DSU dsu = new DSU(n); // 初始化并查集
        long total = 0;       // 累加总成本
        int used = 0;         // 已选用的边数量

        // 遍历所有边，贪心选择能连通两个不同集合的最小边
        for (int[] e : edges) {
            int c = e[0], u = e[1], v = e[2];
            if (dsu.union(u, v)) { // 如果连接了两个不同集合
                total += c;
                used++;
                if (used == n - 1) return (int) total; // 已构成生成树
            }
        }
        return -1; // 遍历完仍未连通所有城市
    }

    public static void main(String[] args) {
        int n = 4;
        int[][] connections = {
                {1, 2, 3},
                {2, 3, 4},
                {3, 4, 5},
                {1, 4, 10},
                {2, 4, 6}
        };
        // Kruskal 输出
        System.out.println(minimumCostKruskal(n, connections)); // 输出 12
        // Prim 输出
        System.out.println(minimumCostPrim(n, connections));    // 输出 12
    }

    /**
     * Prim 算法求最小生成树的总成本
     * @param n 城市数量
     * @param connections 每条连接 [城市1, 城市2, 成本]
     * @return 最小总成本；若无法连通所有城市返回 -1
     */
    public static int minimumCostPrim(int n, int[][] connections) {
        if (n <= 1) return 0;

        // 构建邻接表，每个元素是 (cost, 目标城市)
        List<int[]>[] adj = new ArrayList[n];
        for (int i = 0; i < n; i++) adj[i] = new ArrayList<>();

        // 添加无向边
        for (int[] e : connections) {
            int x = e[0], y = e[1], c = e[2];
            if (x == y) continue; // 忽略自环
            int u = x - 1, v = y - 1;
            adj[u].add(new int[]{c, v});
            adj[v].add(new int[]{c, u});
        }

        boolean[] visited = new boolean[n]; // 标记已加入生成树的城市
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        long total = 0; // 累加总成本
        int count = 1;  // 已访问城市数

        // 从 0 号城市出发，将它的所有边加入最小堆
        visited[0] = true;
        for (int[] edge : adj[0]) pq.offer(edge);

        // 不断从堆中取最小边扩展
        while (!pq.isEmpty() && count < n) {
            int[] top = pq.poll();
            int c = top[0], v = top[1];
            if (visited[v]) continue; // 城市已访问，跳过
            visited[v] = true;
            count++;
            total += c;
            for (int[] edge : adj[v]) pq.offer(edge); // 加入新城市的边
        }

        return count == n ? (int) total : -1; // 判断是否连通
    }
}