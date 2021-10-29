package miniitbts;

import org.cloudbus.cloudsim.Log;

import FCFS_algo.FCFS_Scheduler;
import ITBTS_algo.ITBTS_Scheduler;
import ITBTS_algo.itbtsScheduler;
import SJF_algo.SJF_Scheduler;
import maxMin_algo.maxmin_Scheduler;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	    Log.printLine("========== FCFS ==========");
		FCFS_Scheduler.run();
		Log.printLine("\n\n========== SJF ==========\n");
		SJF_Scheduler.run();
		Log.printLine("\n\n========== ITBTS ==========\n");
		ITBTS_Scheduler.run();
		//itbtsScheduler.run();
		Log.printLine("\n\n========== Max-Min ==========\n");
		maxmin_Scheduler.run();
		
		
	}

}
