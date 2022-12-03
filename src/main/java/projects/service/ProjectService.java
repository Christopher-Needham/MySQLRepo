package projects.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import projects.dao.ProjectDao;
import projects.entity.Project;
import projects.exception.DbException;

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


		/**
		 * This method relays between the Dao layer and the app layer
		 * @param project
		 */
		public void modifyObjectDetails(Project project) {
			if(!projectDao.modifyProjectDetails(project)){
				throw new DbException("Project with ID="
						+ project.getProjectId() + " does not exist.");
			}
			
			
		}

		/**
		 * This method is used relay between the Dao layer and the app layer
		 * 
		 * @param projectId
		 */

		public void deleteProject(Integer projectId) {
			if (!projectDao.deleteProject(projectId)) {
				throw new DbException("Project with ID="
						+ projectId + " does not exist.");
			}
			
		}
	
}
