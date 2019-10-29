/*  Name: <Ali Rashidinejad>
 *  COSC 311  FA19
 *  pp1008
 *  URL:  https://github.com/arashidi21/DonutShopSimulation
 */
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
public class DonutShop {
	Queue<Customer> q=new LinkedList<>();
	Random r=new Random(97);
	int customersCompleted=0;
	int maxWait=0;
	//clears data for new run then prints out wait time and customer data for 20 ticks
	public  void Experiment(int servers, int serveMin, double arrivals) {
		reset();
		Customer inService[]=new Customer[servers];
		int totalWait=0;
		for(int i=0; i<20; i++) {
			doTick(servers, inService, arrivals, serveMin);
			int totalCustomers=getWorkingServers(inService)+customersCompleted+q.size();
			System.out.println("Tick#"+i);
			printTick(inService, totalWait, totalCustomers);
			totalWait+=q.size();
		}
	}
	//Prints data for individual tick
	public void printTick(Customer[] inService, int totalWait, int totalCustomers) {
		System.out.println("\t #Customers in service: "+getWorkingServers(inService));
		System.out.println("\t #Customers with completed service: "+customersCompleted);
		System.out.println("\t #Customers in queue: "+q.size());
		System.out.println("\t Total wait time: "+totalWait);
		if(totalCustomers==0)
			System.out.println("\t Wait time: 0, 0.00, "+maxWait+"\n");
		else
			System.out.printf("\t Wait time: 0, %5.2f, "+maxWait+"\n", (totalWait*1.0)/totalCustomers);
	}
	public void doTick(int servers, Customer[] serving, double arrivals, int serveMin) {
		serviceAndRelease(servers, serving);
		waitIncremented();
		newArrivals(arrivals, serveMin);
		idleToWorking(servers, serving);
	}
	//Decrements service time and if finished will remove Customer from service
	public void serviceAndRelease(int servers, Customer[] serving) {
		for(int j=0; j<servers; j++) {
			if (serving[j] != null) {
				serving[j].serviceTime--;
				if (serving[j].serviceTime == 0) {
					serving[j] = null;
					customersCompleted++;
				}
			}
		}
	}
	//All customers in queue have wait time incremented and compared to max wait
	public void waitIncremented() {
		if (!q.isEmpty()) {
			q.iterator().forEachRemaining(x->x.waitTime++);
			if(q.peek().waitTime>maxWait)
				maxWait=q.peek().waitTime;
		}
	}
	//Poisson distributed new arrivals added to queue with randomly generated serve time
	public void newArrivals(double arrivals, int serveMin) {
		for(int i=getPoissonRandom(arrivals); i>0; i--) {
			q.add(new Customer(0, r.nextInt(serveMin)+1));
		}
	}
	//All idle workers dequeue head of queue and begin working
	public void idleToWorking(int servers, Customer[] serving) {
		for(int i=0; i<servers; i++) {
			if(serving[i] ==null && !q.isEmpty()) {
				serving[i]=q.remove();
			}
		}
	}
	//Counts working servers
	public int getWorkingServers(Customer[] serving) {
		int customersInService=0;
		for(int i=0; i<serving.length; i++) {
			if(serving[i]!=null) {
				customersInService++;
			}
		}
		return customersInService;
	}
	//Clear global data for each run
	public void reset() {
		q.clear();
		customersCompleted=0;
		maxWait=0;
	}
	private int getPoissonRandom(double mean) {
	    double L = Math.exp(-mean);
	    int k = 0;
	    double p = 1.0;
	    do {
	        p = p * r.nextDouble();
	        k++;
	    } while (p > L);
	    return k - 1;
	}
	
	public static void main(String [] args) {
		DonutShop ds=new DonutShop();
		ds.Experiment(6, 12, 2);
	}
}
