package ub.cse.algo;
import ub.cse.algo.util.Pair;
import java.util.*;
import java.math.*;

import static ub.cse.algo.Traversals.bfsPaths;

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
            return a.getFirst() + b.getFirst();
        }
    }



    /**
     * Method that returns the calculated
     * SolutionObject as found by your algorithm
     *
     * @return SolutionObject containing the paths, priorities and bandwidths
     */

    public SolutionObject outputPaths() {
        SolutionObject sol = new SolutionObject();
        /*  Your solution goes here */


        //------------------BFS--------------//

//        sol.paths = bfsPaths(graph,clients);

        //Use normal bfs
        // use a conditional to tell if we should pop a node from the queue or not

        int[] priors = new int[graph.size()];
        Arrays.fill(priors, -1);

        Queue<Integer> searchQueue = new LinkedList<>();
        searchQueue.add(graph.contentProvider);

        System.out.println(graph.get(graph.contentProvider));
        while (!searchQueue.isEmpty()) {
            int node = searchQueue.poll();





            for (int neighbor : graph.get(node)) {
                if (priors[neighbor] == -1 && neighbor != graph.contentProvider) {
                    priors[neighbor] = node;
                    searchQueue.add(neighbor);

                    if (bandwidths.get(searchQueue.peek()) < 4 /* replace constant with something else to decide wheather a node should upgrade */) {
                        /*  */
                        System.out.println("Entered If Statement");
                        //node = searchQueue.iterator().next();
                        //Upgrade bandwidth here with some type of calculation

                    }
                }
            }
        }

        // Get the final shortest paths
//        return pathsFromPriors(clients, priors);


        // 1.Implement Dijkstra to generate our paths, weight can either be our bandwidths or load/bandwidth
        // 2.sol.paths = Traversals.Dijkstra_path(graph,clients,sol.bandwidths);

//        //-------------DIJKSTRA-------------//
//        int[] distance = new int[graph.size()];
//        int[] previous = new int[graph.size()];
//
//        Arrays.fill(distance, Integer.MAX_VALUE);
//        Arrays.fill(previous, -1);
//
//        distance[graph.contentProvider] = 0;
//
//        PriorityQueue<Pair<Integer, Integer>> pq = new PriorityQueue<>(new PairComparator());
//        Set<Integer> visited = new HashSet<>();
//        pq.offer(new Pair<>(0, graph.contentProvider));
//
//        while (!pq.isEmpty()) {
//            Pair<Integer, Integer> currentNode = pq.poll();
//            if (visited.contains(currentNode.getSecond())) {
//                continue;
//            } else {
//                visited.add(currentNode.getSecond());
//
//                for (int neighbor : graph.get(currentNode.getSecond())) {
//                    int newDistance = currentNode.getFirst() + bandwidths.get(neighbor);
//
//                    if (newDistance < distance[neighbor]) {
//                        distance[neighbor] = newDistance;
//                        previous[neighbor] = currentNode.getSecond();
//                        pq.offer(new Pair<>(newDistance, neighbor));
//                    }
//                }
//            }
//        }



        ////-------------Paths-Generation-------------////
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
                path.addFirst(currentNode);
                currentNode = priors[currentNode];
            }

            paths.put(client.id, path);

        }
        sol.paths = paths;


    //------------------- Upgrading Bandwidth -----------------------//


        HashMap<Integer,Integer> hashMap = new HashMap<>();



                for (int j = 0; j < sol.paths.size(); j++) {
                    Client clientKey = clients.get(j);
                    for (int i = 0; i < clients.size(); i++) {



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

                    if (hashMap.get(clientKey.id) > info.bandwidths.get(clientKey.id)) {
                        int delta = (hashMap.get(clientKey.id)) - (info.bandwidths.get(clientKey.id));

                        if (delta > 0) {
                            bandwidths.set(clientKey.id, hashMap.get(clientKey.id)/*info.bandwidths.get(clientKey.id) + 100*/);
                        }

                    }
                }
           // }
        }
//        System.out.println(hashMap);
//        System.out.println(info.costBandwidth);
//        System.out.println(info.bandwidths);
        sol.bandwidths = bandwidths;


                //Revenue with bandwidth upgrade +1 = Revenue: 9513885.0
        //Revenue with bandwidth upgrade +2 = Revenue: 9513885.0
        //Revenue with bandwidth upgrade +3 = Revenue: 9764676.0
        //Revenue with bandwidth upgrade +4 = Revenue: 9934770.0
        //Revenue with bandwidth upgrade +5 = Revenue: 1.0327577E7
        //Revenue with bandwidth upgrade +10 = Revenue: 1.0793537E7
        // Revenue with bandwidth upgrade + 30 = Revenue: 1.1251812E7


        //----------------------- Sorting Priorities of Nodes ---------------------------//


        HashMap<Integer,Integer> priorities = new HashMap<>();
        ArrayList<Integer> lowToHighPriority = new ArrayList<>();
        for (Client c : clients) {


                if (lowToHighPriority.isEmpty()){
                    lowToHighPriority.add(c.id);
                }


                else{
                    for (int i = 1; i < lowToHighPriority.size() ; i++) {

                        Client listClient = clients.get(i);
                        float delayForCurrentClient = c.alpha * info.shortestDelays.get(c.id);

                        //More beta = less chance to unsub
                        if (delayForCurrentClient > listClient.alpha * info.shortestDelays.get(listClient.id)){
                            continue;
                        }

                        if (delayForCurrentClient < listClient.alpha * info.shortestDelays.get(listClient.id)) {
                            lowToHighPriority.add(c.id);
                            break;
                        }


                        if (delayForCurrentClient == listClient.alpha * info.shortestDelays.get(listClient.id)){
                            if (c.beta > listClient.beta){
                                lowToHighPriority.add(c.id);
                                break;
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
