package de.hhu.cs.dbs.propra.presentation.rest;

import org.glassfish.jersey.media.multipart.FormDataParam;
import javax.inject.Inject;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Path("/nutzer")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class NutzerController {

    @Inject
    private DataSource dataSource;

    @Context
    private UriInfo uriInfo;


    @GET
    public List<Map<String, Object>> getUser(@QueryParam("email") String email) throws SQLException {

        Connection connection = dataSource.getConnection();
        List<Map<String, Object>> entities = new ArrayList<>();
        if (email == null || email.isEmpty()) {
            try {
                String sql = "SELECT * FROM nutzer;";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.closeOnCompletion();
                ResultSet resultSet = preparedStatement.executeQuery();
                Map<String, Object> entity;

                while (resultSet.next()) {
                    entity = new LinkedHashMap<>();
                    entity.put("nutzerid", resultSet.getRow());
                    entity.put("email", resultSet.getObject(1));
                    entity.put("passwort", resultSet.getObject(2));
                    entities.add(entity);
                }
                resultSet.close();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
            //close the connection at the very end
            finally {
                try {
                    connection.close();
                } catch (Exception e){
                    System.err.println(e.getMessage());
                }
            }
        } else {
            try {
                String sql = "SELECT * FROM nutzer WHERE LOWER(Email) LIKE LOWER(?);";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.closeOnCompletion();
                preparedStatement.setObject(1, email);
                ResultSet resultSet = preparedStatement.executeQuery();

                Map<String, Object> entity = new LinkedHashMap<>();
                entity.put("nutzerid", getRowId(email,connection));
                entity.put("email", resultSet.getObject(1));
                entities.add(entity);

                resultSet.close();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
            //close the connection at the very end
            finally {
                try {
                    connection.close();
                } catch (Exception e){
                    System.err.println(e.getMessage());
                }
            }
        }
        return entities;
    }

    @POST
    public Response addUser(@NotNull @FormDataParam("email") String email,
                            @NotNull @FormDataParam("passwort") String passwort) throws SQLException {

        Connection connection = dataSource.getConnection();
        try {
            String sql = "INSERT INTO nutzer(Email, Password) VALUES(?,?);";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.closeOnCompletion();
            preparedStatement.setObject(1, email);
            preparedStatement.setObject(2, passwort);
            preparedStatement.executeUpdate();

            return Response.status(Response.Status.CREATED)
                    .header("Location", uriInfo.getAbsolutePathBuilder().path(getRowId(email,connection))).build();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity("message: " + e.getMessage()).build();
        }

        //close the connection at the very end
        finally {
            try {
                connection.close();
            } catch (Exception e){
                System.err.println(e.getMessage());
            }
        }

    }


    //Determines the rowid of a given nutzerid (email)
    private String getRowId(String email, Connection connection) throws SQLException {
        PreparedStatement ps =
                connection.prepareStatement("SELECT rowid FROM nutzer WHERE LOWER(Email) LIKE LOWER(?)");
        ps.setObject(1, email);
        ps.closeOnCompletion();
        ResultSet rs = ps.executeQuery();
        String rowid = rs.getString(1);
        rs.close();
        return rowid;
    }
}