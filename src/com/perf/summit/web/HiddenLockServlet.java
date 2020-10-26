package com.perf.summit.web;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/hiddenlock")
public class HiddenLockServlet extends HttpServlet {
	private static final long serialVersionUID = 4256830568331554689L;
    // Object used for FLAT lock
    private final Object sharedObject = new Object();
    // ReentrantReadWriteLock used for WRITE & READ locks
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    static int n1=0,n2=1,n3=0;    
    static void printFibonacci(long count){    
       if(count>0){    
            n3 = n1 + n2;    
            n1 = n2;    
            n2 = n3;    
            printFibonacci(count-1);    
        }    
    }  
	
    /**
     *  Execution pattern #1
     */
    public void executeTask1() {
         
          // 1. Attempt to acquire a ReentrantReadWriteLock READ lock
          lock.readLock().lock();
     	 System.out.println("[executeTask1] lock.readLock() ");

     	 System.out.println("[executeTask1] Sleep 2 ");

          // Wait 2 seconds to simulate some work...
          try { printFibonacci(serialVersionUID);
          Thread.sleep(2000);}catch (Throwable any) {}
          
         
          try {              
                 // 2. Attempt to acquire a Flat lock...
                 synchronized (sharedObject) {
                	 System.out.println("[executeTask1] Working ");
                 }
          }
          // Remove the READ lock
          finally {
                 lock.readLock().unlock();
          }           
         
          System.out.println("[executeTask1] Work Done!");
    }
   
    /**
     *  Execution pattern #2
     */
    public void executeTask2() {
         
          // 1. Attempt to acquire a Flat lock
          synchronized (sharedObject) {                 
          	 System.out.println("[executeTask2] synchronized ");

         	 System.out.println("[executeTask1] Sleep 2 ");

                 // Wait 2 seconds to simulate some work...
                 try { 
                	 printFibonacci(serialVersionUID);
                	 Thread.sleep(2000);
                 
                 }catch (Throwable any) {}
                
                 
             	 System.out.println("[executeTask2] lock.writeLock() ");

                 // 2. Attempt to acquire a WRITE lock                   
                 lock.writeLock().lock();
                
                 try {
                	 System.out.println("[executeTask2] Working ");                  }
                
                 // Remove the WRITE lock
                 finally {
                        lock.writeLock().unlock();
                 }
          }
         
          System.out.println("[executeTask2] Work Done !!!");   
    }
   
    public ReentrantReadWriteLock getReentrantReadWriteLock() {
          return lock;
    }
    
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String servletThread = null;
		try {
			servletThread = Thread.currentThread().getName();
			// Start Task 1 
	        Thread task1 = new Thread(new Runnable() {
	            public void run() {
	            	executeTask1();
	            }
	        }, "ReadLockFirst");


	        // Start Task 2 
	        Thread task2 = new Thread(new Runnable() {
	            public void run() {
	            	executeTask2();
	            }
	        }, "FlatLockFirst");
	        
	        task1.start();
	        task2.start();

	        
	        
		}catch (Exception e) {
		}
		resp.getWriter().append("Servlet Thread " + servletThread );
	}
}
