package jsprit.core.algorithm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jsprit.core.algorithm.box.SchrimpfFactory;
import jsprit.core.algorithm.io.VehicleRoutingAlgorithms;
import jsprit.core.algorithm.recreate.listener.JobInsertedListener;
import jsprit.core.algorithm.recreate.listener.VehicleSwitchedListener;
import jsprit.core.problem.VehicleRoutingProblem;
import jsprit.core.problem.io.VrpXMLReader;
import jsprit.core.problem.job.Job;
import jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import jsprit.core.problem.solution.route.VehicleRoute;
import jsprit.core.problem.vehicle.Vehicle;
import jsprit.core.util.Solutions;

import org.junit.Test;

public class MeetTimeWindowConstraint_IT {
	
	@Test
	public void whenEmployingVehicleWithDifferentWorkingShifts_nRoutesShouldBeCorrect(){
		VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
		new VrpXMLReader(vrpBuilder).read("src/test/resources/simpleProblem.xml");
		VehicleRoutingProblem vrp = vrpBuilder.build();
		
		VehicleRoutingAlgorithm vra = new SchrimpfFactory().createAlgorithm(vrp);
		vra.setNuOfIterations(100);
		Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();
		
		assertEquals(2,Solutions.bestOf(solutions).getRoutes().size());
	}
	
	@Test
	public void whenEmployingVehicleWithDifferentWorkingShifts_certainJobsCanNeverBeAssignedToCertainVehicles(){
		VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
		new VrpXMLReader(vrpBuilder).read("src/test/resources/simpleProblem.xml");
		VehicleRoutingProblem vrp = vrpBuilder.build();
		
		VehicleRoutingAlgorithm vra = new SchrimpfFactory().createAlgorithm(vrp);
		vra.setNuOfIterations(100);
		final List<Boolean> testFailed = new ArrayList<Boolean>();
		vra.addListener(new JobInsertedListener() {
			
			@Override
			public void informJobInserted(Job job2insert, VehicleRoute inRoute, double additionalCosts, double additionalTime) {
				if(job2insert.getId().equals("1")){
					if(inRoute.getVehicle().getId().equals("19")){
						testFailed.add(true);
					}
				}
				if(job2insert.getId().equals("2")){
					if(inRoute.getVehicle().getId().equals("21")){
						testFailed.add(true);
					}
				}
			}
			
		});
		@SuppressWarnings("unused")
		Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();
		
		assertTrue(testFailed.isEmpty());
	}
	
	@Test
	public void whenEmployingVehicleWithDifferentWorkingShifts_certainVehiclesCanNeverBeAssignedToCertainRoutes(){
		VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
		new VrpXMLReader(vrpBuilder).read("src/test/resources/simpleProblem.xml");
		VehicleRoutingProblem vrp = vrpBuilder.build();
		
		VehicleRoutingAlgorithm vra = new SchrimpfFactory().createAlgorithm(vrp);
		vra.setNuOfIterations(100);
		final List<Boolean> testFailed = new ArrayList<Boolean>();
		vra.addListener(new VehicleSwitchedListener() {
			
			@Override
			public void vehicleSwitched(VehicleRoute vehicleRoute, Vehicle oldVehicle, Vehicle newVehicle) {
				if(oldVehicle==null) return;
				if(oldVehicle.getId().equals("21") && newVehicle.getId().equals("19")){
					for(Job j : vehicleRoute.getTourActivities().getJobs()){
						if(j.getId().equals("1")){
							testFailed.add(true);
						}
					}
				}
				if(oldVehicle.getId().equals("19") && newVehicle.getId().equals("21")){
					for(Job j : vehicleRoute.getTourActivities().getJobs()){
						if(j.getId().equals("2")){
							testFailed.add(true);
						}
					}
				}
			}
			
		});
		
		
		@SuppressWarnings("unused")
		Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();
		System.out.println("failed " + testFailed.size());
		assertTrue(testFailed.isEmpty());
	}
	
	@Test
	public void whenEmployingVehicleWithDifferentWorkingShifts_job2CanNeverBeInVehicle21(){
		VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
		new VrpXMLReader(vrpBuilder).read("src/test/resources/simpleProblem.xml");
		VehicleRoutingProblem vrp = vrpBuilder.build();
		
		VehicleRoutingAlgorithm vra = new SchrimpfFactory().createAlgorithm(vrp);
		vra.setNuOfIterations(100);
		Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();
		
		assertEquals(2,Solutions.bestOf(solutions).getRoutes().size());
	}
	
	@Test
	public void whenEmployingVehicleWithDifferentWorkingShifts_job1ShouldBeAssignedCorrectly(){
		VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
		new VrpXMLReader(vrpBuilder).read("src/test/resources/simpleProblem.xml");
		VehicleRoutingProblem vrp = vrpBuilder.build();
		
		VehicleRoutingAlgorithm vra = new SchrimpfFactory().createAlgorithm(vrp);
		vra.setNuOfIterations(100);
		Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();
		
//		assertEquals(2,Solutions.bestOf(solutions).getRoutes().size());
		assertTrue(containsJob(vrp.getJobs().get("1"),getRoute("21",Solutions.bestOf(solutions))));
	}
	
	@Test
	public void whenEmployingVehicleWithDifferentWorkingShifts_job2ShouldBeAssignedCorrectly(){
		VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
		new VrpXMLReader(vrpBuilder).read("src/test/resources/simpleProblem.xml");
		VehicleRoutingProblem vrp = vrpBuilder.build();
		
		VehicleRoutingAlgorithm vra = new SchrimpfFactory().createAlgorithm(vrp);
		vra.setNuOfIterations(100);
		Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();
		
//		assertEquals(2,Solutions.bestOf(solutions).getRoutes().size());
		assertTrue(containsJob(vrp.getJobs().get("2"),getRoute("19",Solutions.bestOf(solutions))));
	}
	
	
	
	
	@Test
	public void whenEmployingVehicleWithDifferentWorkingShifts_and_vehicleSwitchIsNotAllowed_nRoutesShouldBeCorrect(){
		VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
		new VrpXMLReader(vrpBuilder).read("src/test/resources/simpleProblem.xml");
		VehicleRoutingProblem vrp = vrpBuilder.build();
		
		VehicleRoutingAlgorithm vra = VehicleRoutingAlgorithms.readAndCreateAlgorithm(vrp, "src/test/resources/schrimpf_vehicleSwitchNotAllowed.xml");
		vra.setNuOfIterations(100);
		Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();
		
		assertEquals(2,Solutions.bestOf(solutions).getRoutes().size());
	}
	
	@Test
	public void whenEmployingVehicleWithDifferentWorkingShifts_and_vehicleSwitchIsNotAllowed_certainJobsCanNeverBeAssignedToCertainVehicles(){
		VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
		new VrpXMLReader(vrpBuilder).read("src/test/resources/simpleProblem.xml");
		VehicleRoutingProblem vrp = vrpBuilder.build();
		
		VehicleRoutingAlgorithm vra = VehicleRoutingAlgorithms.readAndCreateAlgorithm(vrp, "src/test/resources/schrimpf_vehicleSwitchNotAllowed.xml");
		vra.setNuOfIterations(100);
		final List<Boolean> testFailed = new ArrayList<Boolean>();
		vra.addListener(new JobInsertedListener() {
			
			@Override
			public void informJobInserted(Job job2insert, VehicleRoute inRoute, double additionalCosts, double additionalTime) {
				if(job2insert.getId().equals("1")){
					if(inRoute.getVehicle().getId().equals("19")){
						testFailed.add(true);
					}
				}
				if(job2insert.getId().equals("2")){
					if(inRoute.getVehicle().getId().equals("21")){
						testFailed.add(true);
					}
				}
			}
			
		});
		@SuppressWarnings("unused")
		Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();
		
		assertTrue(testFailed.isEmpty());
	}
	
	@Test
	public void whenEmployingVehicleWithDifferentWorkingShifts_and_vehicleSwitchIsNotAllowed_certainVehiclesCanNeverBeAssignedToCertainRoutes(){
		VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
		new VrpXMLReader(vrpBuilder).read("src/test/resources/simpleProblem.xml");
		VehicleRoutingProblem vrp = vrpBuilder.build();
		
		VehicleRoutingAlgorithm vra = VehicleRoutingAlgorithms.readAndCreateAlgorithm(vrp, "src/test/resources/schrimpf_vehicleSwitchNotAllowed.xml");
		vra.setNuOfIterations(100);
		final List<Boolean> testFailed = new ArrayList<Boolean>();
		vra.addListener(new VehicleSwitchedListener() {
			
			@Override
			public void vehicleSwitched(VehicleRoute vehicleRoute, Vehicle oldVehicle, Vehicle newVehicle) {
				if(oldVehicle==null) return;
				if(oldVehicle.getId().equals("21") && newVehicle.getId().equals("19")){
					for(Job j : vehicleRoute.getTourActivities().getJobs()){
						if(j.getId().equals("1")){
							testFailed.add(true);
						}
					}
				}
				if(oldVehicle.getId().equals("19") && newVehicle.getId().equals("21")){
					for(Job j : vehicleRoute.getTourActivities().getJobs()){
						if(j.getId().equals("2")){
							testFailed.add(true);
						}
					}
				}
			}
			
		});
		
		
		@SuppressWarnings("unused")
		Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();
		System.out.println("failed " + testFailed.size());
		assertTrue(testFailed.isEmpty());
	}
	
	@Test
	public void whenEmployingVehicleWithDifferentWorkingShifts_and_vehicleSwitchIsNotAllowed_job2CanNeverBeInVehicle21(){
		VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
		new VrpXMLReader(vrpBuilder).read("src/test/resources/simpleProblem.xml");
		VehicleRoutingProblem vrp = vrpBuilder.build();
		
		VehicleRoutingAlgorithm vra = VehicleRoutingAlgorithms.readAndCreateAlgorithm(vrp, "src/test/resources/schrimpf_vehicleSwitchNotAllowed.xml");
		vra.setNuOfIterations(100);
		Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();
		
		assertEquals(2,Solutions.bestOf(solutions).getRoutes().size());
	}
	
	@Test
	public void whenEmployingVehicleWithDifferentWorkingShifts_and_vehicleSwitchIsNotAllowed_job1ShouldBeAssignedCorrectly(){
		VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
		new VrpXMLReader(vrpBuilder).read("src/test/resources/simpleProblem.xml");
		VehicleRoutingProblem vrp = vrpBuilder.build();
		
		VehicleRoutingAlgorithm vra = VehicleRoutingAlgorithms.readAndCreateAlgorithm(vrp, "src/test/resources/schrimpf_vehicleSwitchNotAllowed.xml");
		vra.setNuOfIterations(100);
		Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();
		
		assertEquals(2,Solutions.bestOf(solutions).getRoutes().size());
		assertTrue(containsJob(vrp.getJobs().get("1"),getRoute("21",Solutions.bestOf(solutions))));
	}
	
	@Test
	public void whenEmployingVehicleWithDifferentWorkingShifts_and_vehicleSwitchIsNotAllowed_job2ShouldBeAssignedCorrectly(){
		VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
		new VrpXMLReader(vrpBuilder).read("src/test/resources/simpleProblem.xml");
		VehicleRoutingProblem vrp = vrpBuilder.build();
		
		VehicleRoutingAlgorithm vra = VehicleRoutingAlgorithms.readAndCreateAlgorithm(vrp, "src/test/resources/schrimpf_vehicleSwitchNotAllowed.xml");
		vra.setNuOfIterations(100);
		Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();
		
		assertEquals(2,Solutions.bestOf(solutions).getRoutes().size());
		assertTrue(containsJob(vrp.getJobs().get("2"),getRoute("19",Solutions.bestOf(solutions))));
	}

	private boolean containsJob(Job job, VehicleRoute route) {
		if(route == null) return false;
		for(Job j : route.getTourActivities().getJobs()){
			if(job == j){
				return true;
			}
		}
		return false;
	}

	private VehicleRoute getRoute(String vehicleId, VehicleRoutingProblemSolution vehicleRoutingProblemSolution) {
		for(VehicleRoute r : vehicleRoutingProblemSolution.getRoutes()){
			if(r.getVehicle().getId().equals(vehicleId)){
				return r;
			}
		}
		return null;
	}

}
