package com.ls.celtica.lsdelivryls.Ventes;




public class BonExport extends Bon {
    String etat="exporté";

    public BonExport(String num_fact, String date_fact, double verser, String nom_clt, String code_clt) {
        super(num_fact,date_fact,verser,nom_clt,code_clt);

    }
}
