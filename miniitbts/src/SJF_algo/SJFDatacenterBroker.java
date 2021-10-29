package  SJF_algo;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.lists.VmList;

public class SJFDatacenterBroker extends DatacenterBroker {
	
	  SJFDatacenterBroker(String name) throws Exception {
	        super(name);
	    }
	
	
	  protected void submitCloudlets() {
	
		
		int vmIndex = 0;
		List <Cloudlet> sortList= new ArrayList<Cloudlet>();
		ArrayList<Cloudlet> tempList = new ArrayList<Cloudlet>();
		
		for(Cloudlet cloudlet: getCloudletList())
		{
			tempList.add(cloudlet);
		}
		
	int totalCloudlets=tempList.size();
		for(int i=0;i<totalCloudlets;i++)
		{
	
			Cloudlet smallestCloudlet= tempList.get(0);
			for(Cloudlet checkCloudlet: tempList)
			{
				if(smallestCloudlet.getCloudletLength()>checkCloudlet.getCloudletLength())
				{
					smallestCloudlet= checkCloudlet;
					}
				}
				sortList.add(smallestCloudlet);
				tempList.remove(smallestCloudlet);
				
		}
		
		int count=1;
		for(Cloudlet printCloudlet: sortList)
		{
			Log.printLine(count+".Cloudler Id:"+printCloudlet.getCloudletId()+",Cloudlet Length:"+printCloudlet.getCloudletLength());
		    count++;
		}
		
		
		for (Cloudlet cloudlet : sortList) {
			Vm vm;
			// if user didn't bind this cloudlet and it has not been executed yet
			if (cloudlet.getVmId() == -1) {
				vm = getVmsCreatedList().get(vmIndex);
			} else { // submit to the specific vm
				vm = VmList.getById(getVmsCreatedList(), cloudlet.getVmId());
				if (vm == null) { // vm was not created
					Log.printLine(CloudSim.clock() + ": " + getName() + ": Postponing execution of cloudlet "
							+ cloudlet.getCloudletId() + ": bount VM not available");
					continue;
				}
			}

			Log.printLine(CloudSim.clock() + ": " + getName() + ": Sending cloudlet "
					+ cloudlet.getCloudletId() + " to VM #" + vm.getId());
			cloudlet.setVmId(vm.getId());
			sendNow(getVmsToDatacentersMap().get(vm.getId()), CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
			cloudletsSubmitted++;
			vmIndex = (vmIndex + 1) % getVmsCreatedList().size();
			getCloudletSubmittedList().add(cloudlet);
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
    if (getCloudletList().size() == 0 && cloudletsSubmitted == 0) {
       
        cloudletExecution(cloudlet);
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
