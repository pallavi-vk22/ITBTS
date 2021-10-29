package ITBTS_algo;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;

public class ITBTSDatacenterBroker extends DatacenterBroker {
	
	 public ITBTSDatacenterBroker(String name) throws Exception {
	        super(name);
	    }
	 protected void submitCloudlets() {
			int cls=getCloudletList().size();
			int vmss=getVmList().size();
			//DecimalFormat dft = new DecimalFormat("###.##");
			ArrayList< Cloudlet> tempList = new ArrayList<Cloudlet>();
		       for ( Cloudlet cloudlet:getCloudletList()) {
		    	   tempList.add(cloudlet);
		       }
		       
		       ArrayList<Vm> tList = new ArrayList<Vm>();
		       for ( Vm vm:getVmList()) {
		    	   tList.add(vm);}
		       
		     //calculation of etc
				double[][]etc= new double[cls][vmss];
				for(int i=0;i<cls;i++)
					for(int j=0;j<vmss;j++)
					{
						Cloudlet sample= tempList.get(i);
						Vm v= tList.get(j);
						etc[i][j]=(sample.getCloudletLength())/(v.getMips());
					}
				
				
				/*System.out.println("\nETC Matrix\n");
				for(int i=0;i<cls;i++)
				{
			      for(int j=0;j<vmss;j++)
			      {
				     Log.print(dft.format(etc[i][j])+"     " );
			      }
			      System.out.println();
				}
				*/
				//thresholdvalue calculation
				double[] Th= new double[cls];
				for(int i=0;i<cls;i++)
					for(int j=0;j<vmss;j++)
					{
						Th[i]=Th[i]+etc[i][j];
					}
				for(int i=0;i<cls;i++)
					Th[i]=Th[i]/vmss;
				
				
			/*	System.out.println("\nThreshold\n");
				for(int i=0;i<cls;i++)
				 System.out.println(dft.format(Th[i]));*/
				
				
				
				

				//allocation of maximum and minimum tasks
			
			         double max=0,min=Th[0];
				    int n=0,m=0,s=0,vmno=0,flag1=-1,l=0;
				    double notallocatedth[]=new double[cls];
				    
				    for(int i=0;i<cls;i++)
			    	{
			    		if(max<Th[i])
			    		{
			    			max=Th[i];
			    			n=i;
			    		}
			    		if(min>Th[i])
			    		{
			    			min=Th[i];
			    			m=i;
			    		}
			    		
			    	}
				    
				   double minetc=etc[n][0];
	              	for(int j=0;j<vmss;j++)
	    	        {
	    		        if(etc[n][j]<=Th[n])
	    		        {
	    		            if(minetc>=etc[n][j])
	    		              {
	    		                  minetc=etc[n][j];
	    		                  vmno=j;
	    		                  flag1=j;
	    		              }
	    		        }
	    	        	
	            	}
	              	
	              	
	              	Cloudlet task= tempList.get(n);
	              	Vm mv= tList.get(vmno);
	              	System.out.println("\n");
	              	
	              	if(flag1!=-1)
	              	{
	              		Log.printLine(CloudSim.clock() + ": " + getName() + ": Sending cloudlet "
	        					+ n + " to VM #" + vmno);
	              		task.setVmId(mv.getId());sendNow(getVmsToDatacentersMap().get(mv.getId()), CloudSimTags.CLOUDLET_SUBMIT, task);
	        			cloudletsSubmitted++;
	        			//vmIndex = (vmIndex + 1) % getVmsCreatedList().size();
	        			getCloudletSubmittedList().add(task);
	              	
	              	tempList.remove(task);
	              	notallocatedth[n]=-1;
	              	}
	              	
	            	for(int j=0;j<cls;j++)
	            	{
	            	   
	            	        if(j==n)
	            	          continue;
	            	 
	            	    notallocatedth[j]=Th[j];
	            	}
	              	
	              	
	           
	              	Vm minvm=tList.get(0);
					
					for(Vm checkvm:tList )
					{
						if(minvm.getMips()>checkvm.getMips())
						{
							minvm=checkvm;
						} 
					}
	              	int k=minvm.getId();
	              	if(m!=cls-1) {
					task= tempList.get(m);
					 if(etc[m][k]<=Th[m])
		    	     {
						 
						 Log.printLine(CloudSim.clock() + ": " + getName() + ": Sending cloudlet "
		        					+ m + " to VM #" + vmno);
		              		task.setVmId(minvm.getId());sendNow(getVmsToDatacentersMap().get(minvm.getId()), CloudSimTags.CLOUDLET_SUBMIT, task);
		        			cloudletsSubmitted++;
		        			//vmIndex = (vmIndex + 1) % getVmsCreatedList().size();
		        			getCloudletSubmittedList().add(task);
		              	
		              //	tempList.remove(task);
		              	notallocatedth[m]=-1;
		    	               
		    	     }
			
	              	}
				
					 
					 //allocation of not allocated tasks
					 for(int i=0;i<cls-1;i++)    
	   	             {
	   	                  max=notallocatedth[0];
	    	                for(int j=0;j<cls;j++)
	                    	{
	    			                    if(max<=notallocatedth[j])
		    		                    {
		    		                        	max=notallocatedth[j];
		    		                        	s=j;
		    		                     }

	                    	}
	    	              if(s==cls-1)
	    	              {
	    	            	  l=(l+1)%vmss;
	    	            	  notallocatedth[cls-1]=-1;
	    	            	  continue;
	    	              }
	  
	                     while(l!=-1)
	                     {
	                         if(etc[s][l]<=Th[s])
	                         {
	                        	 task= tempList.get(s);
	                        	 Vm v=tList.get(l);
	                        	 Log.printLine(CloudSim.clock() + ": " + getName() + ": Sending cloudlet "
	                 					+ s + " to VM #" + l);
	                       		task.setVmId(v.getId());sendNow(getVmsToDatacentersMap().get(v.getId()), CloudSimTags.CLOUDLET_SUBMIT, task);
	                 			cloudletsSubmitted++;
	                 			//vmIndex = (vmIndex + 1) % getVmsCreatedList().size();
	                 			getCloudletSubmittedList().add(task);
	                       		notallocatedth[s]=-1;
	    	                    l=(l+1)%vmss;
	    	                   break;
	                         }
	                         else 
	                         {
	                        	 l=(l+1)%vmss;
	                         }
	                         
	                     }
	   	             
	   	          }
				    
				    
	              	
		
			// remove submitted cloudlets from waiting list
			for (Cloudlet cloudlet : getCloudletSubmittedList()) {
				getCloudletList().remove(cloudlet);
			}
		}
	 
		protected void processCloudletReturn(SimEvent ev) {
			Cloudlet cloudlet = (Cloudlet) ev.getData();
			getCloudletReceivedList().add(cloudlet);
			Log.printLine(CloudSim.clock() + ": " + getName() + ": Cloudlet " + cloudlet.getCloudletId()
					+ " received");
			cloudletsSubmitted--;
			if (getCloudletList().size() == 0 && cloudletsSubmitted == 0) { // all cloudlets executed
				Log.printLine(CloudSim.clock() + ": " + getName() + ": All Cloudlets executed. Finishing...");
				clearDatacenters();
				finishExecution();
			} else { // some cloudlets haven't finished yet
				if (getCloudletList().size() > 0 && cloudletsSubmitted == 0) {
					// all the cloudlets sent finished. It means that some bount
					// cloudlet is waiting its VM be created
					clearDatacenters();
					createVmsInDatacenter(0);
				}

			}
		}
		
		 protected void cloudletExecution(Cloudlet cloudlet) {

		        if (getCloudletList().size() == 0 && cloudletsSubmitted == 0) { // all cloudlets executed
		            /* Log.printLine(CloudSim.clock() + ": " + getName() + ": All Cloudlets executed. Finishing..."); */
		            clearDatacenters();
		            finishExecution();
		        } else { // some cloudlets haven't finished yet
		            if (getCloudletList().size() > 0 && cloudletsSubmitted == 0) {
		                // all the cloudlets sent finished. It means that some bount
		                // cloudlet is waiting its VM be created
		                clearDatacenters();
		                createVmsInDatacenter(0);
		            }

		        }
		    }
}
