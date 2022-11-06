package projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import projects.entity.Project;
import projects.exception.DbException;
import provided.util.DaoBase;

public class ProjectDao extends DaoBase {
	
	/*This class uses JDBC to connect to MYSQL database and create entries in the database.
	 * 
	 */
	
	
	@SuppressWarnings("unused")
	private static final String CATAGORY_TABLE = "category";
	private static final String MATERIAL_TABLE = "material";
	private static final String PROJECT_TABLE = "project";
	private static final String PROJECT_CATEGORY_TABLE = "project_category";
	private static final String STEP_TABLE = "step";
	
	
	
	/*This method accepts the information from the ProjectService class and 
	 * returns them ready to be added to MySQL.
	 * It will throw exceptions if the connection fails and if the statement isn't created. 
	 */

	public Project insertProject(Project project) {
		//@formatter:off
	String sql = ""
			+"INSERT INTO " + PROJECT_TABLE + " " 
			+"(project_name, estimated_hours, actual_hours, difficulty, notes) "
			+"VALUES " + "(?,?,?,?,?)";
	//@formatter:on
	
	try(Connection conn = DbConnection.getConnection()){
		startTransaction(conn);
			try(PreparedStatement stmt = conn.prepareStatement(sql)){
				setParameter(stmt, 1, project.getProjectName(), String.class);
				setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
				setParameter(stmt, 4, project.getDifficulty(), Integer.class);
				setParameter(stmt, 5, project.getNotes(), String.class);
				
				stmt.executeUpdate();
				
				Integer projectId = getLastInsertId(conn, PROJECT_TABLE);
				commitTransaction(conn);
				
				project.setProjectId(projectId);
				return project;
				
			}
			catch(Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
	}catch(SQLException e) {
			throw new DbException(e);
	}
		}
	
	
	
	
	
}


