import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
public class DonutShop {
	Queue<Customer> q=new LinkedList<>();
	Random r=new Random(97);
	int maxWait=0;
	int totalWait=0;
	int customersInService=0;
	int customersCompleted=0;
	int totalCustomers=0;
	public  void Experiment(int servers, int serveMin, double arrivals) {
		reset();
		Customer inService[]=new Customer[servers];
		for(int i=0; i<20; i++) {
			doTick(servers, inService, arrivals, serveMin);
			System.out.println("Tick#"+i);
			System.out.println("\t #Customers in service: "+customersInService);
			System.out.println("\t #Customers with completed service: "+customersCompleted);
			System.out.println("\t #Customers in queue: "+q.size());
			System.out.println("\t Total wait time: "+totalWait);
			System.out.printf("\t Wait time: 0, %5.2f, "+maxWait+"\n", (totalWait*1.0)/totalCustomers);
		}
	}
	public void doTick(int servers, Customer[] serving, double arrivals, int serveMin) {
		serviceAndRelease(servers, serving);
		waitIncremented();
		newArrivals(arrivals, serveMin);
		idleToWorking(servers, serving);
	}
	public void idleToWorking(int servers, Customer[] serving) {
		for(int i=0; i<servers; i++) {
			if(serving[i] ==null && !q.isEmpty()) {
				serving[i]=q.remove();
				customersInService++;
			}
		}
	}
	public void newArrivals(double arrivals, int serveMin) {
		for(int i=getPoissonRandom(arrivals); i>=0; i--) {
			q.add(new Customer(0, r.nextInt(serveMin)+1));
			totalCustomers++;
		}
	}
	public void waitIncremented() {
		if (!q.isEmpty()) {
			for (int i = q.size(); i > 0; i--) {
				Customer tmp = q.remove();
				tmp.waitTime++;
				q.add(tmp);
				if(tmp.waitTime>maxWait) {
					maxWait=tmp.waitTime;
				}
			}
			totalWait+=q.size();
		}
	}
	public void serviceAndRelease(int servers, Customer[] serving) {
		for(int j=0; j<servers; j++) {
			if (serving[j] != null) {
				serving[j].serviceTime--;
				if (serving[j].serviceTime == 0) {
					serving[j] = null;
					customersCompleted++;
					customersInService--;
				}
			}
		}
	}
	public void reset() {
		q.clear();
		maxWait=0;
		totalWait=0;
		customersInService=0;
		customersCompleted=0;
		totalCustomers=0;
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
		ds.Experiment(8, 12, 2);
		ds.Experiment(1, 3, .25);
	}
}
