package ub.cse.algo;
import ub.cse.algo.util.Pair;
import java.util.*;
import java.math.*;

public class Solution {

    private Info info;
    private Graph graph;
    private ArrayList<Client> clients;
    private ArrayList<Integer> bandwidths;

    /**
     * Basic Constructor
     *
     * @param info: data parsed from input file
     */
    public Solution(Info info) {
        this.info = info;
        this.graph = info.graph;
        this.clients = info.clients;
        this.bandwidths = info.bandwidths;
    }

    private static class PairComparator implements Comparator<Pair<Integer,Integer>> {
        @Override
        public int compare(Pair<Integer,Integer> a, Pair<Integer,Integer> b){
            return -a.getFirst() - b.getFirst();
        }
    }
//
//    static HashMap<Integer,ArrayList<Integer>> Dijkstra_path(Graph graph, ArrayList<Client> clients , ArrayList<Integer> bandwidths){
//        int[] distance =  new int[graph.size()];
//        int[] previous = new int[graph.size()];
//
//        Arrays.fill(distance,Integer.MAX_VALUE);
//        Arrays.fill(previous,-1);
//
//        distance[graph.contentProvider] = 0;
//
//
//        PriorityQueue<Pair<Integer,Integer>> priorityQueue = new PriorityQueue<>(new PairComparator()); //change from minheap to maxheap
//        Set<Integer> visited = new HashSet<>();
//        Collections.reverseOrder()
//
//        priorityQueue.offer(new Pair<>(0,graph.contentProvider)); // (distance, node)
//        System.out.println(graph.contentProvider);
//
//
//        while(!priorityQueue.isEmpty()) {
//            Pair<Integer, Integer> currentNode = priorityQueue.poll();
//            if (visited.contains(currentNode.getSecond())) {
//                continue;
//
//            } else {
//                visited.add(currentNode.getSecond());
//
//                for (Integer neighbor : graph.get(currentNode.getSecond())) { //
//                    int newDistance = currentNode.getFirst() + bandwidths.get(neighbor);
//
//                    if (newDistance < distance[neighbor]) {
//                        distance[neighbor] = newDistance;
//                        previous[neighbor] = currentNode.getSecond();
//                        //priorityQueue.offer(...); <--- WORK ON THIS PART
//                        priorityQueue.offer(new Pair<>(newDistance,neighbor));
//                    }
//                }
//            }
//        }
//        return pathsFromPriors(clients,previous);
//    }


    /**
     * Method that returns the calculated
     * SolutionObject as found by your algorithm
     *
     * @return SolutionObject containing the paths, priorities and bandwidths
     */

    public SolutionObject outputPaths() {
        SolutionObject sol = new SolutionObject();
        /*  Your solution goes here */



        // 1.Implement Dijkstra to generate our paths, weight can either be our bandwidths or load/bandwidth
//        sol.paths = Traversals.Dijkstra_path(graph,clients,sol.bandwidths);

        //-------------DIJKSTRA-------------//
        int[] distance = new int[graph.size()];
        int[] previous = new int[graph.size()];

        Arrays.fill(distance, Integer.MAX_VALUE);
        Arrays.fill(previous, -1);

        distance[graph.contentProvider] = 0;

        PriorityQueue<Pair<Integer, Integer>> pq = new PriorityQueue<>(new PairComparator());
        Set<Integer> visited = new HashSet<>();
        pq.offer(new Pair<>(0, graph.contentProvider));

        while (!pq.isEmpty()) {
            Pair<Integer, Integer> currentNode = pq.poll();
            if (visited.contains(currentNode.getSecond())) {
                continue;
            } else {
                visited.add(currentNode.getSecond());

                for (int neighbor : graph.get(currentNode.getSecond())) {
                    int newDistance = currentNode.getFirst() + bandwidths.get(neighbor);

                    if (newDistance < distance[neighbor]) {
                        distance[neighbor] = newDistance;
                        previous[neighbor] = currentNode.getSecond();
                        pq.offer(new Pair<>(newDistance, neighbor));
                    }
                }
            }
        }


        //-------------Paths-Generation-------------//
        HashMap<Integer, ArrayList<Integer>> paths = new HashMap<>(clients.size());
        // For every client, traverse the prior array, creating the path
        for (Client client : clients) {

            ArrayList<Integer> path = new ArrayList<>();
            int currentNode = client.id;

            while (currentNode != -1) {
                /*
                    Add this ID to the beginning of the
                    path so the path ends with the client
                 */
                path.add(0, currentNode);
                currentNode = previous[currentNode];
            }

            paths.put(client.id, path);

        }
        sol.paths = paths;


    //------------------- Upgrading Bandwidth -----------------------//

//        HashMap<Client,Integer> hashMap = new HashMap<>();
        HashMap<Integer,Integer> hashMap = new HashMap<>();

        for (int i = 0; i < clients.size(); i++) {
            Client clientKey = clients.get(i);

            if (sol.paths.get(i) != null) {
                for (int j = 0; j < sol.paths.get(i).size(); j++) {


                    if (!hashMap.containsKey(clientKey.id)) {
                        hashMap.put(clientKey.id, 1);
                    }

                    if (hashMap.containsKey(clientKey.id)) {
                        Integer oldLoad = hashMap.get(clientKey.id);
                        oldLoad += 1;
                        hashMap.put(clientKey.id, oldLoad);
                    }
                }

                if (hashMap.get(clientKey.id) != null) {

                    if (hashMap.get(clientKey.id) > bandwidths.get(clientKey.id)) {
                        int delta = Math.max(0, (bandwidths.get(clientKey.id) + 5) - (bandwidths.get(clientKey.id)));

                        if (delta > 0) {
                            bandwidths.set(clientKey.id, bandwidths.get(clientKey.id) + 5);
                        }
                    }
                }
            }
        }
        sol.bandwidths = bandwidths;




        //----------------------- Sorting Priorities of Nodes ---------------------------//


        HashMap<Integer,Integer> priorities = new HashMap<>();
        ArrayList<Integer> lowToHighPriority = new ArrayList<>();
        for (Client c : clients) {

            if (sol.paths.containsKey(c.id)){

                if (lowToHighPriority.isEmpty()){
                    lowToHighPriority.add(c.id);
                }


                else{
                    for (int i = 1; i < lowToHighPriority.size() ; i++) {



                        Client listClient = clients.get(i);

                        if (c.payment < listClient.payment){
                            continue;
                        }

                        if (c.payment > listClient.payment) {
                            lowToHighPriority.add(c.id);
                        }

                        if (c.payment == listClient.payment){
                            if (c.beta > listClient.beta){
                                lowToHighPriority.add(c.id);
                            }
                            else {
                                lowToHighPriority.add(i +1 ,c.id);
                            }
                        }
                    }
                }
            }


        }

        for (int i = 0; i < lowToHighPriority.size(); i++) {
            priorities.put(lowToHighPriority.get(i),i);
        }
        sol.priorities = priorities;

//        for(int i = 0; i < graph.size(); i++){
//            for(int j = 0; j < clients.size(); j++){
//
//                Client clientAsKey = clients.get(j);
//                if(graph.get(i).contains(j)){
//
//                    if(!hashMap.containsKey(clientAsKey)){
//                        hashMap.put(clientAsKey,1);
//                    } else {
//                        Integer val = hashMap.get(clientAsKey);
//                        val += 1;
//                        hashMap.put(clientAsKey,val);
//                    }
//                }
//
//                if(hashMap.get(clientAsKey) != null){
//                    if(hashMap.get(clientAsKey) > bandwidths.get(clientAsKey.id)){
//                        int val = Math.max(0,(bandwidths.get(clientAsKey.id) + 5) - (bandwidths.get(clientAsKey.id)) );
//
//                        if(val > 0){
//                            int oldBandwidth = (bandwidths.get(clientAsKey.id) );
//                            bandwidths.set(clientAsKey.id, bandwidths.get(clientAsKey.id) + 5);
//                        }
//                    }
//                }
//            }
//        }
//        sol.bandwidths = bandwidths;



        return sol;
    }
}
