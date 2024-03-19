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

@Path("/entwickler")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class EntwicklerController {

    @Inject
    private DataSource dataSource;

    @Context
    private UriInfo uriInfo;

    @Context
    private NutzerController nutzerController;

    @Context
    private ProgrammierspracheController programmierspracheController;


    @GET
    public List<Map<String, Object>> getEntwickler(@QueryParam("kuerzel") @DefaultValue("") String kuerzel) throws SQLException {

        PreparedStatement preparedStatement;
        List<Map<String, Object>> entities = new ArrayList<>();
        Connection connection = dataSource.getConnection();
        try {

            String sql = "SELECT n.ROWID AS nutzerid," +
                         " s.ROWID AS spezialistid," +
                         " e.ROWID AS entwicklerid," +
                         " e.Email," +
                         " n.Password," +
                         " s.Verfuegbarkeitsstatus," +
                         " e.kuerzel\n" +
                         "FROM entwickler e, nutzer n, spezialist s\n" +
                         "WHERE e.Email = n.Email AND e.Email=s.Email";

            if (!kuerzel.isEmpty()) {
                sql = sql + "  AND e.kuerzel=?;";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setObject(1, kuerzel);
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
                entity.put("spezialistid", resultSet.getObject(2));
                entity.put("entwicklerid", resultSet.getObject(3));
                entity.put("email", resultSet.getObject(4));
                entity.put("passwort", resultSet.getObject(5));
                entity.put("verfuegbarkeitsstatus", resultSet.getObject(6));
                entity.put("kuerzel", resultSet.getObject(7));
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
    public Response addEntwickler(@NotNull @FormDataParam("email") String email,
                                  @NotNull @FormDataParam("passwort") String passwort,
                                  @NotNull @FormDataParam("verfuegbarkeitsstatus") String verfuegbarkeitsstatus,
                                  @NotNull @FormDataParam("kuerzel") String kuerzel,
                                  @NotNull @FormDataParam("benennung") String benennung) throws SQLException {

        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);

        try {

            //try to add spezialist (and user) if they don't exist
            try{
                if(nutzerController.getUser(email).isEmpty()){
                    String sql1 = "INSERT INTO nutzer VALUES(?,?);";
                    executeGivenQuery(email,passwort,connection,sql1);
                }

                //Insert the spezialist into the db NOTE: if the spezalist already exists it throws an exception
                //because a spezialist must be either an entwickler or a designer.
                String sql2 = "INSERT INTO spezialist VALUES(?,?);";
                executeGivenQuery(email, verfuegbarkeitsstatus, connection, sql2);

            } catch(SQLException e) {
                System.err.println(e.getMessage());
                connection.rollback();
                return Response.status(Response.Status.BAD_REQUEST).entity("message: " + e.getMessage()).build();
            }

            //Insert Entwickler into the db
            String sql3 = "INSERT INTO entwickler VALUES(?,?);";
            executeGivenQuery(kuerzel, email,connection,sql3);

            connection.commit();

            //add the programming language to the database if it doesn't exist
            int programmierspracheid = programmierspracheController.getProgrammierspracheId(benennung);
            if(programmierspracheid < 0) {
                programmierspracheController.addProgrammiersprache(benennung);
                programmierspracheid = programmierspracheController.getProgrammierspracheId(benennung);
            }

            //add the programming language to the beherrscht schema
            programmierspracheController.addProgrammierspracheToEntwickler(kuerzel, programmierspracheid);

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


    //Determines the rowid of a given entwicklerid (email)
    private String getRowId(String email, Connection connection) throws SQLException {
        PreparedStatement ps =
                connection.prepareStatement("SELECT rowid FROM entwickler WHERE Email LIKE ?");
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
