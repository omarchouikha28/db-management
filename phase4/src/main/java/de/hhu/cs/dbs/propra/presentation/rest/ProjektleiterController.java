package de.hhu.cs.dbs.propra.presentation.rest;

import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.inject.Inject;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Path("/projektleiter")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ProjektleiterController {

    @Inject
    private DataSource dataSource;

    @Context
    private UriInfo uriInfo;

    @Context
    private NutzerController nutzerController;

    @GET
    public List<Map<String, Object>> getProjektleiter(
            @QueryParam("email") @DefaultValue("") String email,
            @QueryParam("gehalt") @DefaultValue("") String gehalt) throws SQLException {

        PreparedStatement preparedStatement;
        List<Map<String, Object>> entities = new ArrayList<>();
        Connection connection = dataSource.getConnection();
        try {
            String sql = "SELECT n.rowid, pl.rowid, * FROM projektleiter pl, nutzer n\n" +
                         "WHERE pl.Email = n.Email";

            if (!gehalt.isEmpty() && !email.isEmpty()) {
                sql = sql + " AND pl.Email LIKE ? AND pl.Gehalt > ?;";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setObject(1, email);
                preparedStatement.setObject(2, gehalt);
            } else if (!email.isEmpty()) {
                sql = sql + " AND pl.Email LIKE LOWER(?);";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setObject(1, email);
            } else if (!gehalt.isEmpty()) {
                sql = sql + " AND pl.Gehalt > ?;";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setObject(1, gehalt);
            } else {
                sql = sql + ";";
                preparedStatement = connection.prepareStatement(sql);
            }
            preparedStatement.closeOnCompletion();
            ResultSet resultSet = preparedStatement.executeQuery();

            Map<String, Object> entity;
            while (resultSet.next()) {
                entity = new LinkedHashMap<>();
                entity.put("nutzerid", resultSet.getObject(1));
                entity.put("projektleiterid", resultSet.getObject(2));
                entity.put("email", resultSet.getObject(3));
                entity.put("passwort", resultSet.getObject(6));
                entity.put("gehalt", resultSet.getObject(4));
                entities.add(entity);
            }
            resultSet.close();

            return entities;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return entities;
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

    @POST
    //transactional
    public Response addProjektleiter(@NotNull @FormDataParam("email") String email,
                                     @NotNull @FormDataParam("passwort") String passwort,
                                     @NotNull @FormDataParam("gehalt") String gehalt) throws SQLException {

        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);

        try {

            //add the user to the database if it doesn't exist
            if(nutzerController.getUser(email).isEmpty()){
                String sql1 = "INSERT INTO nutzer VALUES(?,?);";
                executeGivenQuery(email,passwort,connection,sql1);
            }

            String sql = "INSERT INTO projektleiter VALUES(?,?);";
            executeGivenQuery(email,gehalt,connection,sql);

            connection.commit();

            return Response.status(Response.Status.CREATED)
                    .header("Location", uriInfo.getAbsolutePathBuilder().path(getRowId(email,connection))).build();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            connection.rollback();
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

    //Determines the rowid of a given projektleiterid (email)
    private String getRowId(String email, Connection connection) throws SQLException {
        PreparedStatement ps =
                connection.prepareStatement("SELECT rowid FROM projektleiter WHERE Email LIKE ?");
        ps.setObject(1, email);
        ps.closeOnCompletion();
        ResultSet rs = ps.executeQuery();
        String rowid = rs.getString(1);
        rs.close();
        return rowid;
    }

    private void executeGivenQuery(String param1, String param2, Connection connection, String sql) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.closeOnCompletion();
        preparedStatement.setObject(1, param1);
        preparedStatement.setObject(2, param2);
        preparedStatement.executeUpdate();
    }
}
