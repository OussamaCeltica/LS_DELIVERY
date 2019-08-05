package com.ls.celtica.lsdelivryls.Ventes;

/**
 * Created by celtica on 14/02/19.
 */

public class BonCommandeExport extends BonCommande {
    String etat="exporté";

    public BonCommandeExport(String code, String date, double versement, String nom_clt, String id_clt) {
        super(code, date, versement, nom_clt, id_clt);
    }
}
