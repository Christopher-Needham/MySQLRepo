package projects.service;

import projects.dao.ProjectDao;
import projects.entity.Project;

public class ProjectService {
	
	private static ProjectDao projectDao = new ProjectDao();
	
	
		/** This method acts as a relay between the SQl connection page and the main 
		 * app. It accepts the information here and send it to the Dao class. 
		 */
	public static Project addProject(Project project) {
		
		
		return projectDao.insertProject(project);
	}
	

}
