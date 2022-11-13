package projects.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import projects.dao.ProjectDao;
import projects.entity.Project;

public class ProjectService {
	
	private ProjectDao projectDao = new ProjectDao();
	
	
		/** This method acts as a relay between the SQl connection page and the main 
		 * app. It accepts the information here and send it to the Dao class. 
		 */
	public Project addProject(Project project) {
		return projectDao.insertProject(project);
	}



		public Project fetchProjectById(Integer projectId) {
			return projectDao.fetchProjectByInt(projectId).orElseThrow(
					() -> new NoSuchElementException(
					"Project with project ID=" + projectId + " does not exist."));		}



		public List<Project> fetchAllProjects() {
			return projectDao.fetchAllProjects();
		}
	
}
