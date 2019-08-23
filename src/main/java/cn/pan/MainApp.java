package cn.pan;

import cn.pan.dao.NewsJDBC;
import cn.pan.model.NewsDoc;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class MainApp {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        NewsJDBC newsJDBC = (NewsJDBC) context.getBean("newsJDBC");
        NewsDoc newsDoc = newsJDBC.getNewsDoc(5914881);
        System.out.println(newsDoc.getNews_title());


    }

}
