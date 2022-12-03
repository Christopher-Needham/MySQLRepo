package projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import projects.entity.Category;
import projects.entity.Material;
import projects.entity.Project;
import projects.entity.Step;
import projects.exception.DbException;
import provided.util.DaoBase;

public class ProjectDao extends DaoBase {
	
	/*This class uses JDBC to connect to MYSQL database and create entries in the database.
	 * 
	 */
	
	
	@SuppressWarnings("unused")
	private static final String CATEGORY_TABLE = "category";
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



	public List<Project> fetchAllProjects() {
		String Sql = "SELECT * FROM " + PROJECT_TABLE + " ORDER BY project_name";
		
		try(Connection conn = DbConnection.getConnection()){
			startTransaction(conn);
				try(PreparedStatement stmt = conn.prepareStatement(Sql)){
					try(ResultSet rs = stmt.executeQuery()){
						List<Project> projects = new LinkedList<>();
						while(rs.next()) {
							Project project = new Project();
							
							project.setActualHours(rs.getBigDecimal("actual_hours"));
							project.setDifficulty(rs.getObject("difficulty",Integer.class));
							project.setEstimatedHours(rs.getBigDecimal("estimated_hours"));
							project.setNotes(rs.getString("notes"));
							project.setProjectId(rs.getObject("project_id",Integer.class));
							project.setProjectName(rs.getString("project_name"));
							
							projects.add(project);
						}
						return projects;
					}
					
				}
				catch(Exception e) {
					rollbackTransaction(conn);
					throw new DbException(e);
				}
		}
		catch(SQLException e){
			throw new DbException(e);
			
		}
		
	}



	public Optional<Project> fetchProjectByInt(Integer projectId) {
		String Sql = "SELECT * FROM " + PROJECT_TABLE + " WHERE project_id = ?";
			try(Connection conn = DbConnection.getConnection()){
				startTransaction(conn);
					try{
						Project project = null;
						try(PreparedStatement stmt = conn.prepareStatement(Sql)){
							setParameter(stmt, 1, projectId, Integer.class);
							try(ResultSet rs = stmt.executeQuery()){
								if(rs.next()) {
									project = extract(rs, Project.class);
								}
							}
						}
						if(Objects.nonNull(project)) {
							project.getMaterials().addAll(fetchMaterialForProject(conn, projectId));
							project.getSteps().addAll(fetchStepsForProject(conn, projectId));
							project.getCategories().addAll(fetchCategoriesForProject(conn, projectId));
						}
						commitTransaction(conn);
						
						return Optional.ofNullable(project);
					}
					catch(Exception e) {
						rollbackTransaction(conn);
						throw new DbException(e);
					}
				
				
			}
			catch(SQLException e) {
				throw new DbException(e);
			}
	}
	private Collection<? extends Category> fetchCategoriesForProject(Connection conn, Integer projectId) throws SQLException {
		//@formatter:off
				String sql = ""
						+ "SELECT * FROM " + CATEGORY_TABLE + " m "
						+ "JOIN " + PROJECT_CATEGORY_TABLE + " pc USING (category_id) "
						+ "WHERE project_id = ?";
				//@formatter:on
				
				try(PreparedStatement stmt = conn.prepareStatement(sql)){
					setParameter(stmt, 1, projectId, Integer.class);
					
					try(ResultSet rs = stmt.executeQuery()){
						List<Category> categories = new LinkedList<>();
						
						while(rs.next()) {
							categories.add(extract(rs, Category.class));
						}
						return categories;
					}
					
				}
			}



	private Collection<? extends Step> fetchStepsForProject(Connection conn, Integer projectId)throws SQLException {
				//@formatter:off
				String sql = ""
						+ "SELECT * FROM " + STEP_TABLE + " WHERE project_id = ?";
				//@formatter:on
				
				try(PreparedStatement stmt = conn.prepareStatement(sql)){
					setParameter(stmt, 1, projectId, Integer.class);
					
					try(ResultSet rs = stmt.executeQuery()){
						List<Step> steps = new LinkedList<>();
						
						while(rs.next()) {
							steps.add(extract(rs, Step.class));
						}
						return steps;
					}
					
				}
			}



	private List<Material> fetchMaterialForProject(Connection conn, Integer projectId) throws SQLException{
		//@formatter:off
		String sql = ""
				+ "SELECT * FROM " + MATERIAL_TABLE + " WHERE project_id = ?";
		//@formatter:on
		
		try(PreparedStatement stmt = conn.prepareStatement(sql)){
			setParameter(stmt, 1, projectId, Integer.class);
			
			try(ResultSet rs = stmt.executeQuery()){
				List<Material> materials = new LinkedList<>();
				
				while(rs.next()) {
					materials.add(extract(rs, Material.class));
				}
				return materials;
			}
			
		}
	}

	/**
	 * This method creates the sql statement to update the project in the server
	 * @param project
	 * @return boolean
	 */

	public boolean modifyProjectDetails(Project project) {
		//@formatter:off
		String sql = ""
				+ "UPDATE " + PROJECT_TABLE + " SET "
				+ "project_name = ?, "
				+ "estimated_hours = ?, "
				+ "actual_hours = ?, "
				+ "difficulty = ?, "
				+ "notes = ? "
				+ "WHERE project_id = ?";
		//@formatter:on
		
		try(Connection conn = DbConnection.getConnection()){
			startTransaction(conn);
			
				try(PreparedStatement stmt = conn.prepareStatement(sql)){
					setParameter(stmt, 1, project.getProjectName(), String.class);
					setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
					setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
					setParameter(stmt, 4, project.getDifficulty(), Integer.class);
					setParameter(stmt, 5, project.getNotes(), String.class);
					setParameter(stmt, 6, project.getProjectId(), Integer.class);
					
					boolean modified = stmt.executeUpdate() == 1;
					commitTransaction(conn);
					
					return modified;
					
				
			 }
				catch(Exception e) {
					rollbackTransaction(conn);
					throw new DbException(e);
				}
		}
		catch(SQLException e) {
			throw new DbException(e);
		}
				
	}


	/**
	 * This method creates the sql statement to delete the project on the server. 
	 * @param projectId
	 * @return boolean
	 */
	public boolean deleteProject(Integer projectId) {
		
		String sql = ""
				+ "DELETE FROM " + PROJECT_TABLE + " WHERE project_id = ?";
		
			try (Connection conn = DbConnection.getConnection()){
				startTransaction(conn);
				try(PreparedStatement stmt = conn.prepareStatement(sql)){
					setParameter(stmt, 1, projectId, Integer.class);
					
					boolean deleted = stmt.executeUpdate() == 1;
					commitTransaction(conn);
					
					return deleted;
					
				}
				catch(Exception e) {
		rollbackTransaction(conn);
					throw new DbException(e);
				}
				
			}
			catch(SQLException e) {
				throw new DbException(e);
			}
		
	}

	
}


