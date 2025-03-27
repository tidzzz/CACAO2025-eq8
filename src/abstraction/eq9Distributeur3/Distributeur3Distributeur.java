package abstraction.eq9Distributeur3;

import abstraction.eqXRomu.clients.ClientFinal;
import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.filiere.IDistributeurChocolatDeMarque;
import abstraction.eqXRomu.general.Variable;
import abstraction.eqXRomu.general.VariablePrivee;
import abstraction.eqXRomu.produits.ChocolatDeMarque;
import abstraction.eqXRomu.produits.Gamme;

import java.util.HashMap;
import java.util.List;

public class Distributeur3Distributeur extends Distributeur3Acteur implements IDistributeurChocolatDeMarque {
    // Implémentée par Héloïse
    // protected HashMap<ChocolatDeMarque, Double> stocks;
    protected HashMap<ChocolatDeMarque, Float> prix;
    protected VariablePrivee stockTotal;
    private VariablePrivee stockBQ;
    private VariablePrivee stockBQ_E;


    public Distributeur3Distributeur() {
        super();
        this.stockChocoMarque = new HashMap<>();
        prix = new HashMap<>();
        this.stockTotal = new VariablePrivee("équipe 9 stock total",this);
        this.stockBQ = new VariablePrivee("équipe 9 stock BQ",this);
        this.stockBQ_E = new VariablePrivee("équipe 9 stock BQ_E",this);
        //System.out.println("crypto constructeur : "+this.cryptogramme);
    }

    @Override
    public void initialiser() {
        super.initialiser();
        List<ChocolatDeMarque> produits = Filiere.LA_FILIERE.getChocolatsProduits();
        double quantiteinit = 300.0;

        for (ChocolatDeMarque cm : produits) {
            if (cm.getGamme().equals(Gamme.BQ)) {
                this.stockChocoMarque.put(cm, quantiteinit);
                this.journalActeur.ajouter("stock de base " + quantiteinit + " de " + cm.getNom());
                stockTotal.ajouter(this,quantiteinit,this.cryptogramme);
                if(cm.getChocolat().isEquitable()){
                    stockBQ_E.ajouter(this,quantiteinit,this.cryptogramme);

                    this.prix.put(cm, 2500.0F);
                }else{
                    stockBQ.ajouter(this,quantiteinit,this.cryptogramme);
                    this.prix.put(cm, 3000.0F);
                }
            }
        }
    }


    @Override
    public double prix(ChocolatDeMarque choco) {
        if(this.stockChocoMarque.containsKey(choco)) {
            return prix.get(choco);
        }else{
            return -1;
        }
    }


    @Override
    public double quantiteEnVente(ChocolatDeMarque choco, int crypto) {

        if (this.cryptogramme==crypto && this.stockChocoMarque.containsKey(choco)) {
            if(this.stockChocoMarque.get(choco)>=100) {
                this.journalActeur.ajouter("Mise en rayon de 100 tonnes de "+choco.getNom());
                //System.out.println("demande quantite vente "+choco.getNom()+" tonnes :"+100);
                return 100;
            }else{
                //System.out.println("demande quantite vente "+choco.getNom()+" tonnes :"+this.stockChocoMarque.get(choco));
                this.journalActeur.ajouter("Mise en rayon de "+this.stockChocoMarque.get(choco)+" (max) de "+choco.getNom());
                return this.stockChocoMarque.get(choco);
            }
        } else {
            //System.out.println("demande quantite vente "+choco.getNom()+" tonnes :"+0);
            return 0.0;
        }
    }

    @Override
    public double quantiteEnVenteTG(ChocolatDeMarque choco, int crypto) {
        //System.out.println(choco.getNom()+"en tête de gondole");
        return this.quantiteEnVente(choco,crypto)*ClientFinal.POURCENTAGE_MAX_EN_TG;
    }

    @Override
    public void vendre(ClientFinal client, ChocolatDeMarque choco, double quantite, double montant, int crypto) {
        if(crypto==this.cryptogramme){
            stockChocoMarque.put(choco,Double.valueOf(this.stockChocoMarque.get(choco)-quantite));
            this.MAJStocks();
            journalDeVente.ajouter("Vente de "+quantite+" tonnes de "+choco.toString()+" à "+client.getNom()+" pour "+montant+" euros");
        }
    }

    // A éventuellement supprimer
    public void MAJStocks(){
        double total = 0.0;
        double BQ = 0.0;
        double BQ_E = 0.0;
        for(ChocolatDeMarque choco : stockChocoMarque.keySet()){
            total+=stockChocoMarque.get(choco);
            if(choco.getGamme().equals(Gamme.BQ)) {
                if (choco.isEquitable()){
                    BQ_E += stockChocoMarque.get(choco);
                }else{
                    BQ += stockChocoMarque.get(choco);
                }
            }


        }
        this.stockTotal.setValeur(this,total,this.cryptogramme);
        this.stockBQ.setValeur(this,BQ,this.cryptogramme);
        this.stockBQ_E.setValeur(this,BQ_E,this.cryptogramme);
    }

    public List<Variable> getIndicateurs() {
        List<Variable> res = super.getIndicateurs();
        res.add(this.stockBQ);
        res.add(this.stockBQ_E);
        res.add(this.stockTotal);
        return res;
    }

    @Override
    public void notificationRayonVide(ChocolatDeMarque choco, int crypto) {
        this.journalActeur.ajouter("Le rayon de "+choco.toString()+" est vide ");
    }

    @Override
    public void next() {
        this.journalStocks.ajouter("Stock Total avant  : "+this.stockTotal.getValeur(this.cryptogramme));
        super.next();
        this.journalStocks.ajouter("Stock Total après : "+this.stockTotal.getValeur(this.cryptogramme));
    }
}
