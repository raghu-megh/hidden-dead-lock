package com.perf.summit.web;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/lock")
public class LockServlet extends HttpServlet {
	private static final long serialVersionUID = 4256830568331554689L;
	//private static	final  long LOOPCOUNT = 1000;
	
	final Lock lock = new ReentrantLock(true);

	
    synchronized void intrinsicLock() {
        Thread th = new Thread(new Runnable() {
            public void run() {
                intrinsicLock();
            }
        }, "FlatLockTester");
        th.start();
//        try {
//            th.join();
//        } catch (InterruptedException e) {
//        }
    }
    
    void reentrantLock() {
        lock.lock();
        Thread th = new Thread(new Runnable() {
            public void run() {
                reentrantLock();
            }
        }, "ReentrantLockTester");
        th.start();
//        try {
//            th.join();
//        } catch (InterruptedException e) {
//        }
        lock.unlock();
    }
    
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String lock = "FlatLock";
		if(req.getParameter("lock")!=null) {
			lock = "reentrantLock";
			reentrantLock();			
		}else {
			intrinsicLock();
		}
		String servletThread = null;
		try {
			servletThread = Thread.currentThread().getName();
		}catch (Exception e) {
		}
		resp.getWriter().append("Servlet Thread " + servletThread + " is using lock  : " + lock);
	}
}