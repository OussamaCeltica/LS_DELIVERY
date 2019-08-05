package com.ls.celtica.lsdelivryls;

import android.os.AsyncTask;
import android.os.StrictMode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;


public class MyBDJava {
    
     private Connection connect=null;
       private Statement s=null;
        private PreparedStatement p=null;
         ResultSet r=null;
         doAfterBeforeConnect doCon;

        // public String typeBD,lien;
    
    public MyBDJava(String lien, String typeDB, doAfterBeforeConnect d) throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {
      doCon=d;


      new doIt().execute(typeDB,lien);

     }

    //AsynchTask pour rétablir la connexion ..
    class doIt extends AsyncTask<String,String,String> {


        @Override
        protected String doInBackground(String... strings) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);


            try {
                Class.forName(strings[0]);
                connect = DriverManager.getConnection(strings[1]);
                s = connect.createStatement();
            } catch (ClassNotFoundException e) {
                doCon.echec();
                e.printStackTrace();
                return "NO";
            } catch (SQLException e) {
                e.printStackTrace();
                doCon.echec();
                return "NO";
            }
            return "OK";

        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            doCon.before();
        }

        @Override
        protected void onPostExecute(String r) {
            super.onPostExecute(r);
            if(r.equals("OK")){
                try {
                    doCon.After();
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }


        }
    }

        /*
        //read from DB  , //le retour de resultas on l acced avec Instance de  ResultSet r=MyBDJava.r
         et on manipule avec: while(r.next()){ r.getString(nom_Column); }

         */
        public ResultSet read(String query, doAfterBeforeGettingData d) throws SQLException, InterruptedException, ExecutionException, TimeoutException {

            new DBselect(d).execute(query);

            return r;
        }


        //write into db ..
        public void write(String query, doAfterBeforeGettingData doIt) throws SQLException {

            new DBinsert(doIt).execute(query);

        }

        //dans l insertion  pour que le thread ne bloque pas ..
        class DBinsert extends AsyncTask<String, Boolean, String> {
            doAfterBeforeGettingData doThis;

            public DBinsert(doAfterBeforeGettingData d) {
                doThis = d;
            }

            @Override
            protected String doInBackground(String... strings) {

                try {
                    connect.prepareStatement(strings[0]).executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                    return "false";
                }

                return "true";
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                doThis.before();
            }

            @Override
            protected void onPostExecute(String r) {
                super.onPostExecute(r);
                if(r.equals("true")){
                    try {
                        doThis.After();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }



            }


        }


        // Class de selection pour que le thread ne bloque pas ..
        class DBselect extends AsyncTask<String, String, String> {
            doAfterBeforeGettingData doThis;

            public DBselect(doAfterBeforeGettingData d) {
                doThis = d;
            }

            @Override
            protected String doInBackground(String... strings) {
                try {
                    r = s.executeQuery(strings[0]);
                } catch (SQLException e) {
                    e.printStackTrace();
                    doThis.echec();
                    return "false";

                }
                return "true";
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                doThis.before();
            }

            @Override
            protected void onPostExecute(String r) {
                super.onPostExecute(r);
                if(r.equals("true")){

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                doThis.After();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }

                        }
                    }).start();


                }
            }

        }


    public interface doAfterBeforeGettingData{
        public void echec();
        public void before();
        public void After() throws SQLException;
    }

    public interface doAfterBeforeConnect{
        public void echec();
        public void before();
        public void After() throws SQLException, InterruptedException, ExecutionException, TimeoutException;

    }


    
}
