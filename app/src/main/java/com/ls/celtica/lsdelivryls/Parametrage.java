package com.ls.celtica.lsdelivryls;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ls.celtica.lsdelivryls.Ventes.AfficherVentes;

import java.sql.SQLException;
import java.util.ArrayList;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class Parametrage extends AppCompatActivity {
    private EditText nom,mdp;
    private LinearLayout div_admin;
    private ScrollView div_param;
    private LinearLayout div_sql;
    SpinnerDialog spinnerLangue;
    ArrayList<String> langues=new ArrayList<String>();
    ArrayList<String> codesLangues=new ArrayList<String>();
    EditText ip,port,bd,user,mdp2;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parametrage);
        if (savedInstanceState != null) {
            //region Revenir a au Deviceconfig ..
            Intent intent = new Intent(getApplicationContext(), DeviceConfig.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            //endregion
        }else {

            progress=new ProgressDialog(Parametrage.this);


            //region Conexion d admin ..
            div_admin = (LinearLayout) findViewById(R.id.param_div_admin);
            div_param = (ScrollView) findViewById(R.id.param_div_param);


            nom = (EditText) findViewById(R.id.param_admin_nom);
            mdp = (EditText) findViewById(R.id.param_admin_mdp);
            ((TextView) findViewById(R.id.param_admin_conn)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (nom.getText().toString().equals("") || mdp.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.remplissage_err), Toast.LENGTH_SHORT).show();
                    } else {
                        Cursor r = Accueil.bd.read("select * from admin where pseudo='" + nom.getText().toString().replaceAll("'", "") + "'");
                        if (r.moveToNext()) {
                            if (r.getString(r.getColumnIndex("mdp")).equals(mdp.getText().toString().replaceAll("'", ""))) {
                                div_admin.setVisibility(View.GONE);
                                div_param.setVisibility(View.VISIBLE);

                            } else {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.param_mdp_err), Toast.LENGTH_SHORT).show();

                            }

                        } else {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.param_user_err), Toast.LENGTH_SHORT).show();

                        }

                    }

                }
            });

            //endregion

            //region changer la permission de modifier les prix ..
            ((LinearLayout)findViewById(R.id.param_prixEdit)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder mb = new AlertDialog.Builder(Parametrage.this); //c est l activity non le context ..

                    View v= getLayoutInflater().inflate(R.layout.div_check_price_edit,null);
                    RadioGroup check=(RadioGroup) v.findViewById(R.id.radio_check);
                    final RadioButton oui=(RadioButton)v.findViewById(R.id.check_price_oui);
                    final RadioButton non=(RadioButton)v.findViewById(R.id.check_price_non);
                    mb.setView(v);
                    final AlertDialog ad=mb.create();

                    if(Login.user.isEdit){
                        oui.setChecked(true);
                    }else {
                        non.setChecked(true);
                    }

                    check.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup radioGroup, int i) {
                           if( radioGroup.getCheckedRadioButtonId() == oui.getId()){
                               Login.user.changeEditPricePermission(true);

                           }else {
                               Login.user.changeEditPricePermission(false);
                           }
                           ad.dismiss();
                        }
                    });


                    ad.show();


                }
            });
            //endregion

            //region changer la permission de faire des remises ..
            ((LinearLayout)findViewById(R.id.param_remise)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder mb = new AlertDialog.Builder(Parametrage.this); //c est l activity non le context ..

                    View v= getLayoutInflater().inflate(R.layout.div_remise_permission,null);
                    RadioGroup check=(RadioGroup) v.findViewById(R.id.radio_check);
                    final RadioButton oui=(RadioButton)v.findViewById(R.id.check_remise_oui);
                    final RadioButton non=(RadioButton)v.findViewById(R.id.check_remise_non);
                    mb.setView(v);
                    final AlertDialog ad=mb.create();

                    if(Login.user.isRemise){
                        oui.setChecked(true);
                    }else {
                        non.setChecked(true);
                    }

                    check.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup radioGroup, int i) {

                            if( radioGroup.getCheckedRadioButtonId() == oui.getId()){
                                Login.user.changeRemisePermission(true);

                            }else {
                                Login.user.changeRemisePermission(false);
                            }
                            ad.dismiss();

                        }
                    });



                    ad.show();
                    //endregion
                }
            });

            //endregion

            //region aficher les archives des vente | commande | retour ..
            ((LinearLayout)findViewById(R.id.param_bon_archive)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i=new Intent(Parametrage.this, AfficherVentes.class);
                    i.putExtra("Demande","archive");
                    startActivity(i);
                }
            });

            //endregion

            //region changer la langue ..
            //region remplissage de spinner dédié au langues
            langues.add("Français");
            codesLangues.add("fr");
            langues.add("Arabe");
            codesLangues.add("ar");
            langues.add("Anglais");
            codesLangues.add("en");

            spinnerLangue=new SpinnerDialog(Parametrage.this,langues,"Search..",R.style.DialogAnimations_SmileWindow,"Close");// With 	Animation
            spinnerLangue.bindOnSpinerListener(new OnSpinerItemClick() {
                @Override
                public void onClick(String item, int position) {
                    Accueil.bd.write("update langue set lang='"+codesLangues.get(position)+"'");

                    Intent intent = new Intent(getApplicationContext(), DeviceConfig.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }
            });
            //endregion

            ((LinearLayout)findViewById(R.id.param_change_lang)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                            spinnerLangue.showSpinerDialog();


                }
            });

            //endregion

            //region configurer le serveur ..
            div_sql=(LinearLayout)findViewById(R.id.DivSqlConnect);
            ((LinearLayout)findViewById(R.id.param_configserveur)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    div_param.setVisibility(View.GONE);
                    div_sql.setVisibility(View.VISIBLE);

                    ip=(EditText)findViewById(R.id.synch_ip);
                    port=(EditText)findViewById(R.id.synch_port);
                    user=(EditText)findViewById(R.id.synch__user);
                    bd=(EditText)findViewById(R.id.synch_bd);
                    mdp2=(EditText)findViewById(R.id.synch_mdp);

                    Cursor r5= Accueil.bd.read("select * from sqlconnect");

                    if(Login.user.serveur.ip != null) {
                        ip.setText(Login.user.serveur.ip);
                        port.setText(Login.user.serveur.port);
                        user.setText(Login.user.serveur.user);
                        bd.setText(Login.user.serveur.bdName);
                        mdp2.setText(Login.user.serveur.mdp);
                    }
                }
            });

            //endregion

            //region connecter sql server
            ((TextView)findViewById(R.id.synch_Butt)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Accueil.BDsql=new SqlServerBD(ip.getText().toString(), port.getText().toString(), bd.getText().toString(), user.getText().toString(), mdp2.getText().toString(), "net.sourceforge.jtds.jdbc.Driver", new SqlServerBD.doAfterBeforeConnect() {
                            @Override
                            public void echec() {
                                progress.dismiss();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.sync_conect_err),Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void before() {
                                progress.setTitle("Connexion");
                                progress.setMessage("attendez SVP...");
                                progress.show();
                            }

                            @Override
                            public void After() throws SQLException {
                                progress.dismiss();
                                Login.user.serveur.majInfos(ip.getText().toString(), port.getText().toString(), bd.getText().toString(), user.getText().toString(), mdp2.getText().toString());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.sync_conect_succ),Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        });
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }


                }
            });
            //endregion

            //region change  type device ..
            ((LinearLayout)findViewById(R.id.param_SCannerChange)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder mb = new AlertDialog.Builder(Parametrage.this); //c est l activity non le context ..

                    View v= getLayoutInflater().inflate(R.layout.change_type_device,null);
                    RadioGroup check=(RadioGroup) v.findViewById(R.id.check);
                    RadioButton sans=(RadioButton)v.findViewById(R.id.type_sans);
                    RadioButton avec=(RadioButton)v.findViewById(R.id.type_avec);
                    mb.setView(v);
                    final AlertDialog ad=mb.create();
                    ad.show();
                    ad.setCanceledOnTouchOutside(false); //ne pas fermer on click en dehors ..
                    //ad.setCancelable(false); //désactiver le button de retour ..

                    if(Login.user.type_device.equals(getResources().getString(R.string.login_device_type2))){
                        sans.setChecked(true);
                    }else {
                        avec.setChecked(true);
                    }


                    check.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup ch, int i) {
                            if(ch.getCheckedRadioButtonId() == R.id.type_avec){
                               Login.user.type_device=getResources().getString(R.string.login_device_type1);
                                Accueil.bd.write("update admin set type_device='"+getResources().getString(R.string.login_device_type1)+"'");
                            }else {
                                Login.user.type_device=getResources().getString(R.string.login_device_type2);
                                Accueil.bd.write("update admin set type_device='"+getResources().getString(R.string.login_device_type2)+"'");
                            }
                            ad.dismiss();
                        }
                    });


                }
            });

            //endregion

            //region changer le nom de company ..
            ((LinearLayout)findViewById(R.id.param_change_companyName)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder mb = new AlertDialog.Builder(Parametrage.this); //c est l activity non le context ..

                    View v= getLayoutInflater().inflate(R.layout.div_input_msg,null);
                    TextView valider=(TextView) v.findViewById(R.id.div_inp_msg_valider);
                    TextView msg=(TextView) v.findViewById(R.id.div_inp_msg_msg);
                    final EditText nom=(EditText)v.findViewById(R.id.div_inp_msg_inp);

                    msg.setText(getResources().getString(R.string.param_companyName_change));
                    nom.setText(Login.user.nom_company+"");
                    nom.setHint(getResources().getString(R.string.param_companyName_changeHInt));

                    mb.setView(v);
                    final AlertDialog ad=mb.create();
                    ad.show();
                    ad.setCanceledOnTouchOutside(false); //ne pas fermer on click en dehors ..


                    valider.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!nom.getText().toString().equals("")){
                                Login.user.changeCompanyName(nom.getText().toString());
                                ad.dismiss();
                            }else {
                                Toast.makeText(getApplicationContext(),getResources().getString(R.string.remplissage_err),Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                }
            });

            //endregion

            //region changer le nom de printer ..
            ((LinearLayout)findViewById(R.id.param_change_printerName)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder mb = new AlertDialog.Builder(Parametrage.this); //c est l activity non le context ..

                    View v= getLayoutInflater().inflate(R.layout.div_input_msg,null);
                    TextView valider=(TextView) v.findViewById(R.id.div_inp_msg_valider);
                    TextView msg=(TextView) v.findViewById(R.id.div_inp_msg_msg);
                    final EditText nom=(EditText)v.findViewById(R.id.div_inp_msg_inp);

                    msg.setText(getResources().getString(R.string.param_printerName_change));
                    nom.setText(Login.user.nom_printer+"");

                    mb.setView(v);
                    final AlertDialog ad=mb.create();
                    ad.show();
                    ad.setCanceledOnTouchOutside(false); //ne pas fermer on click en dehors ..


                    valider.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!nom.getText().toString().equals("")){
                                Login.user.changePrinterName(nom.getText().toString());
                                ad.dismiss();
                            }else {
                                Toast.makeText(getApplicationContext(),getResources().getString(R.string.remplissage_err),Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                }
            });

            //endregion

            //region changer le mot de passe d admin ..
            ((LinearLayout)findViewById(R.id.param_change_adminMdp)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //region Affichage de div de changement ..
                    AlertDialog.Builder mb = new AlertDialog.Builder(Parametrage.this); //c est l activity non le context ..
                    View v= getLayoutInflater().inflate(R.layout.change_admin_mdp,null);
                    TextView valider=(TextView) v.findViewById(R.id.param_changeMdp_valider);
                    final EditText nomAdmin=(EditText)v.findViewById(R.id.param_changeMdp_nomAdmin);
                    final EditText newMdp=(EditText)v.findViewById(R.id.param_changeMdp_new);
                    final EditText oldMdp=(EditText)v.findViewById(R.id.param_changeMdp_actuel);
                    mb.setView(v);
                    final AlertDialog ad=mb.create();
                    ad.show();
                    nomAdmin.setText(nom.getText());
                    //endregion

                    //region valider le changement ..
                    valider.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(oldMdp.getText().toString().equals("") || newMdp.getText().toString().equals("") || nomAdmin.getText().toString().equals("")){
                                Toast.makeText(getApplicationContext(),getResources().getString(R.string.remplissage_err),Toast.LENGTH_SHORT).show();

                            }else {
                                Cursor r=Accueil.bd.read("select * from admin");
                                while (r.moveToNext()){
                                    if (oldMdp.getText().toString().equals(r.getString(r.getColumnIndex("mdp")))){
                                        Accueil.bd.write("update admin set pseudo='"+nomAdmin.getText().toString().replaceAll("'","")+"' , mdp='"+newMdp.getText().toString().replaceAll("'","")+"'");
                                        ad.dismiss();
                                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.param_admin_ChangeMdp_succes),Toast.LENGTH_SHORT).show();

                                    }else {
                                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.param_admin_ChangeMdp_err),Toast.LENGTH_SHORT).show();

                                    }
                                }
                            }

                        }
                    });
                    //endregion
                }
            });

            //endregion

            //region affichage le nom de device ..
            ((LinearLayout)findViewById(R.id.param_deviceNom)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //region afficher le div de changement ..
                    AlertDialog.Builder mb = new AlertDialog.Builder(Parametrage.this); //c est l activity non le context ..

                    View v=  getLayoutInflater().inflate(R.layout.div_device_name,null);
                    TextView valider=(TextView) v.findViewById(R.id.device_new_nom_valider);
                    TextView nomActuel=(TextView) v.findViewById(R.id.device_nom_actuel);
                    final EditText newNom=(EditText)v.findViewById(R.id.device_new_nom);

                    mb.setView(v);
                    final AlertDialog ad=mb.create();
                    ad.show();
                    ad.setCanceledOnTouchOutside(false); //ne pas fermer on click en dehors ..

                    //endregion

                    //region afficher le nom actuel ..
                    Cursor r=Accueil.bd.read("select * from device ");
                    if(r.moveToNext()){
                        nomActuel.setText(getResources().getString(R.string.param_deviceNomActuelHint)+"  "+r.getString(r.getColumnIndex("id_device")));
                    }

                    //endregion

                    //region valider le nouveau nom ..
                    valider.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(newNom.getText().toString().equals("")){
                                Toast.makeText(getApplicationContext(),getResources().getString(R.string.remplissage_err),Toast.LENGTH_SHORT).show();
                            }else {
                                Accueil.bd.write("update device set id_device='"+newNom.getText().toString().replaceAll("'","")+"'");
                                ad.dismiss();
                                Toast.makeText(getApplicationContext(),getResources().getString(R.string.param_admin_ChangeMdp_succes),Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                    //endregion

                }
            });

            //endregion

            //region afficher le code vendor actuel et code camion ..
            ((LinearLayout)findViewById(R.id.param_user_actuel)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder mb = new AlertDialog.Builder(Parametrage.this); //c est l activity non le context ..

                    View v= getLayoutInflater().inflate(R.layout.div_user_actuel,null);
                    TextView code_vendeur=(TextView) v.findViewById(R.id.div_user_actuel_vendeur);
                    TextView code_camion=(TextView) v.findViewById(R.id.div_user_actuel_camion);

                    Cursor r=Accueil.bd.read("select * from utilisateur");
                    if(r.moveToNext()){
                        code_vendeur.setText(getResources().getString(R.string.param_userActuelVendeur_Lab)+" "+r.getString(r.getColumnIndex("code_vendeur")));
                        code_camion.setText(getResources().getString(R.string.param_userActuelCamion_Lab)+" "+r.getString(r.getColumnIndex("code_camion")));
                    }

                    mb.setView(v);
                    final AlertDialog ad=mb.create();
                    ad.show();

                }
            });
            //endregion

            //region ajouter un produit avec les prix
            ((LinearLayout)findViewById(R.id.param_AddPr)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //deprecated ..
                    /*
                    if(Login.user.code_camion.equals(getResources().getString(R.string.login_user_descCamion))){
                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.import_produit_noUser),Toast.LENGTH_SHORT).show();
                    }else {
                        startActivity(new Intent(Parametrage.this,AddProduit.class));
                    }
                    */

                }
            });

            //endregion

            //region le dernier camion qui fait des ventes ..
            ((LinearLayout)findViewById(R.id.param_last_camion)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder mb = new AlertDialog.Builder(Parametrage.this); //c est l activity non le context ..

                    View v= getLayoutInflater().inflate(R.layout.div_user_actuel,null);
                    TextView nom_camion=(TextView) v.findViewById(R.id.div_user_actuel_vendeur);
                    TextView code_camion=(TextView) v.findViewById(R.id.div_user_actuel_camion);

                    Cursor r=Accueil.bd.read("select c.* from camion_actuel ca inner join camion c on ca.code_camion=c.code_camion");
                    if(r.moveToNext()){
                        nom_camion.setText(r.getString(r.getColumnIndex("nom_camion")));
                        code_camion.setText(getResources().getString(R.string.param_userActuelCamion_Lab)+" "+r.getString(r.getColumnIndex("code_camion")));
                    }else {
                        nom_camion.setText("");
                    }

                    mb.setView(v);
                    final AlertDialog ad=mb.create();
                    ad.show();
                }
            });

            //endregion

            //region formater la BD
            ((LinearLayout)findViewById(R.id.param_format)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder mb = new AlertDialog.Builder(Parametrage.this); //c est l activity non le context ..

                    View v= getLayoutInflater().inflate(R.layout.confirm_box,null);
                    TextView msg=(TextView) v.findViewById(R.id.confirm_msg);
                    TextView oui=(TextView) v.findViewById(R.id.confirm_oui);
                    TextView non=(TextView) v.findViewById(R.id.confirm_non);

                    msg.setText("Voulez vous vraiment supprimez ! ");

                    mb.setView(v);
                    final AlertDialog ad=mb.create();
                    ad.show();
                    ad.setCanceledOnTouchOutside(false); //ne pas fermer on click en dehors ..
                    ad.setCancelable(false); //désactiver le button de retour ..

                    oui.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Login.user.formaterBD();
                            ad.dismiss();
                            Toast.makeText(getApplicationContext(),"Réinitialisation terminé.",Toast.LENGTH_SHORT).show();
                        }
                    });

                    non.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ad.dismiss();
                        }
                    });



                }
            });

            //endregion

            //region Fermer la session de vendeur
            ((LinearLayout) findViewById(R.id.param_deconect)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Accueil.bd.write("delete from utilisateur where '1'='1' ");
                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);


                }
            });

            //endregion
        }
    }

    @Override
    public void onBackPressed() {
        if(div_sql.getVisibility()== View.VISIBLE) {
            div_sql.setVisibility(View.GONE);
            div_param.setVisibility(View.VISIBLE);
        }
        else super.onBackPressed();
    }
}
