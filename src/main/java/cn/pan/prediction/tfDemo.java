package cn.pan.prediction;

import org.tensorflow.*;

public class tfDemo {
    public static void main(String[] args) {
//        Map<String, Integer> map = new HashMap<String, Integer>();
//        Map<Integer,String> mp = new HashMap<>();
//
//        try{
//            BufferedReader br = new BufferedReader(new InputStreamReader(
//                    new FileInputStream(new File("vocab.txt"))));
//            String lineTxt = null;
//            int idx = 0;
//            while ((lineTxt = br.readLine())!=null){
//                map.put(lineTxt,idx);
//                idx++;
//            }
//            br.close();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        int[] input = {103, 334};

        SavedModelBundle savedModelBundle = SavedModelBundle.load("./src/main/resources/mymodel","myTag");
//        Graph graph = savedModelBundle.graph();
//        Iterator<Operation> opIterator = graph.operations();
//        System.out.println("operations --- ");
//        while (opIterator.hasNext()){
//            System.out.println(opIterator.next());
//        }
        Session sess = savedModelBundle.session();
        Tensor<?> X_input = Tensor.create(input);
        //top_k:0 values top_k:1 indices
        Tensor tensor = sess.runner().feed("X_input", X_input).fetch("top_k:1").run().get(0);
        int [][] ans = new int[2][5];
        tensor.copyTo(ans);

        Tensor similarity = sess.runner().feed("X_input", X_input).fetch("predict_Sim").run().get(0);
        float[][] sim = new float[2][10695];
        similarity.copyTo(sim);
        System.out.println(getMax(sim[0]));

        for (Integer val:
             ans[0]) {
            System.out.println(val);
        }

    }

    public static float getMax(float[] sim){
        float max = 0;
        int index = 0;
        for (int i = 0; i < sim.length; i++) {
            if (sim[i]>max){
                max = sim[i];
                index = i;
            }
        }
        return index;
    }

}
