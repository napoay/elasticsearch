package cn.pan.prediction;

import cn.pan.controller.IndexController;
import org.junit.Test;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

public class tfModel {

    public static int [] getMaxWords(int input){
        int[] inputs = new int[1];
        inputs[0] = input;
        SavedModelBundle savedModelBundle = SavedModelBundle.load("./src/main/resources/mymodel","myTag");
        Session sess = savedModelBundle.session();
        Tensor<?> X_input = Tensor.create(inputs);
        Tensor indices = sess.runner().feed("X_input", X_input).fetch("top_k:1").run().get(0);
        int [][] words = new int[1][5];
        indices.copyTo(words);
        return words[0];

    }

    @Test
    public void Test() throws Exception{
        IndexController.word2int = new HashMap<>();
        BufferedReader br = new BufferedReader(new FileReader("D:\\Github\\esfilesearch\\src\\main\\resources\\mymodel\\allWords.txt"));
        int count = 0;
        String line = null;
        while ((line = br.readLine())!=null){
            IndexController.word2int.put(line, count);
            count ++;
        }
        int index = IndexController.word2int.get("media");
        System.out.println(index);
        int[] maxWords = getMaxWords(index);
    }
}
