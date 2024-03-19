package de.hhu.cs.dbs.propra.presentation.rest;


import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.annotation.security.RolesAllowed;
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

@Path("/kunden")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class KundeController {

    @Inject
    private DataSource dataSource;

    @Context
    private UriInfo uriInfo;

    @Context
    private NutzerController nutzerController;



    @GET
    public List<Map<String, Object>> getKunde(
            @QueryParam("email") @DefaultValue("") String email,
            @QueryParam("telefonnummer") @DefaultValue("") String telefonnummer) throws SQLException {

        PreparedStatement preparedStatement;
        List<Map<String, Object>> entities = new ArrayList<>();
        Connection connection = dataSource.getConnection();
        try {
            String sql = "SELECT n.rowid, k.rowid, * FROM kunde k, nutzer n\n" +
                         "WHERE k.Email = n.Email";

            if (!telefonnummer.isEmpty() && !email.isEmpty()) {
                sql = sql + " AND LOWER(k.Email) LIKE LOWER(?) AND k.Telefonnummer LIKE ?;";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setObject(1, email);
                preparedStatement.setObject(2, telefonnummer);
            } else if (!email.isEmpty()) {
                sql = sql + " AND LOWER(k.Email) LIKE LOWER(?);";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setObject(1, email);
            } else if (!telefonnummer.isEmpty()) {
                sql = sql + " AND k.Telefonnummer LIKE ?;";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setObject(1, telefonnummer);
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
                entity.put("kundeid", resultSet.getObject(2));
                entity.put("email", resultSet.getObject(3));
                entity.put("passwort", resultSet.getObject(6));
                entity.put("telefonnummer", resultSet.getObject(4));
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
    public Response addKunde(@NotNull @FormDataParam("email") String email,
                             @NotNull @FormDataParam("passwort") String passwort,
                             @NotNull @FormDataParam("telefon") String telefonnummer) throws SQLException {

        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);

        try {

            //add the user to the database if it doesn't exist
            if(nutzerController.getUser(email).isEmpty()){
                String sql1 = "INSERT INTO nutzer VALUES(?,?);";
                executeGivenQuery(email,passwort,connection,sql1);
            }

            //insert the kunde into the db
            String sql = "INSERT INTO kunde VALUES(?,?);";
            executeGivenQuery(email, telefonnummer, connection, sql);

            connection.commit();

            return Response.status(Response.Status.CREATED)
                    .header("Location", uriInfo.getAbsolutePathBuilder().path(getRowId(email, connection))).build();
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

    private void executeGivenQuery(String param1, String param2, Connection connection, String sql) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.closeOnCompletion();
        preparedStatement.setObject(1, param1);
        preparedStatement.setObject(2, param2);
        preparedStatement.executeUpdate();
    }

    //Determines the rowid of a given kundenid (email)
    private String getRowId(String email, Connection connection) throws SQLException {
        PreparedStatement ps =
                connection.prepareStatement("SELECT rowid FROM kunde WHERE LOWER(Email) LIKE LOWER(?)");
        ps.setObject(1, email);
        ps.closeOnCompletion();
        ResultSet rs = ps.executeQuery();
        String rowid = rs.getString(1);
        rs.close();
        return rowid;
    }
}
