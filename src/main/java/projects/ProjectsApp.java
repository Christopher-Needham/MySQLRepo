package projects;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

//Main app to capture user input to populate data in SQL Database

public class ProjectsApp {
	
		private Scanner scanner = new Scanner(System.in);
		private ProjectService projectService = new ProjectService();
		private Project curProject;
	
	
	//@formatter:off
		private List<String> operations = List.of(
				"1) Add a Project",
				"2) List Projects",
				"3) Select a project"
				);
		//@formatter:on
		
		
			
	public static void main(String[] args) {
		
		new ProjectsApp().processUserSelections();
	}
	
	
	//This method provides the user a selection, captures his/her input, and closes when finished. 
	
	private void processUserSelections() {
		boolean done = false;
		while(!done) {
			try {
				int selection = getUserSelection();
				
				switch (selection) {
					case -1:
						done = exitMenu();
					break;
					case 1:
						createProject();
						break;
					case 2:
						listProjects();
						break;
					case 3:
						selectProject();
						break;
					default:
						System.out.println("\n" + selection + " is not a valid selection. Try again.");
					break;
				}
			}
			catch(Exception e) {
				System.out.println("\nError: " + e + " Try again.");
			}
			
			
		}
		
	}
	
	private void selectProject() {
			listProjects();
			
			Integer projectId = getIntInput("Enter a project ID to select a project");
			
			curProject = null;
			
			curProject = projectService.fetchProjectById(projectId);
			
			if(curProject == null) {
				System.out.println("Invalid project ID selected");
			}
			
	}
	
		
		private void listProjects() {
		List<Project> projects = projectService.fetchAllProjects();
		
		System.out.println("\nProjects:");
		
		projects.forEach(project -> System.out
				.println("  " + project.getProjectId() + ": " + project.getProjectName()));
	}
		
		/**This method prints the options to the user and sets that data 
		 * as the proper inputs. 
		 */
	
	private void createProject() {
		String projectName = getStingInput("Enter the project name");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours");
		Integer difficulty = getIntInput("Enter the project difficulty (1-5)");
		String notes = getStingInput("Enter project notes");
		
		Project project = new Project();
		
		project.setProjectName(projectName);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);
		
		Project dbProject = projectService.addProject(project);
		System.out.println("You have successfully created project: " + dbProject);
		
		
	}
	
	/**Gathers data from the user and gives errors when that data isn't correct
	 * 
	 * @param prompt
	 * @return
	 */
	
	private BigDecimal getDecimalInput(String prompt) {
		String input = getStingInput(prompt);
		if(Objects.isNull(input)) {
		return null;
		}
		try {
			return new BigDecimal(input).setScale(2);
				
			}
		catch(NumberFormatException e) {
			throw new DbException(input + " is not a valid decimal number.");
		}
	}
	
	//This method closes out the menu app if the users hits enter.
	
	private boolean exitMenu() {
		System.out.println("Exiting the menu.");
		
		return true;
	}
	
	/**Gathers data from the user and gives errors when that data isn't correct
	 * 
	 * @param prompt
	 * @return
	 */
	
	private int getUserSelection() {
			printOperations();
			
			Integer input = getIntInput("Enter a menu selection");
			
		return Objects.isNull(input) ? -1 : input;
	}

		//This method prints out the menu to the console for the opperator
	
	private void printOperations() {
		System.out.println("\nThese are the available selections. Press the Enter key to quit:");
		operations.forEach(line -> System.out.println(" " + line));
		
		if(Objects.isNull(curProject)) {
			System.out.println("\nYou are not working with a project");
		}
		else {
			System.out.println("\nYou are working with project: " + curProject);
		}
		
	}
	/**Gathers data from the user and gives errors when that data isn't correct
	 * 
	 * @param prompt
	 * @return
	 */
	
	private Integer getIntInput(String prompt) {
		String input = getStingInput(prompt);
		if(Objects.isNull(input)) {
		return null;
		}
		try {
			return Integer.valueOf(input);
				
			}
		catch(NumberFormatException e) {
			throw new DbException(input + " is not a valid number.");
		}
	}
	
	/**Gathers data from the user and gives errors when that data isn't correct
	 * 
	 * @param prompt
	 * @return
	 */
	
	private String getStingInput(String prompt) {
		System.out.print(prompt + ": ");
		String input = scanner.nextLine();
		
		return input.isBlank() ?null : input.trim();
		
	}
	
}
