package abstraction.eq9Distributeur3;

import abstraction.eqXRomu.contratsCadres.*;
import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.filiere.IActeur;
import abstraction.eqXRomu.produits.ChocolatDeMarque;
import abstraction.eqXRomu.produits.Gamme;
import abstraction.eqXRomu.produits.IProduit;

import java.util.ArrayList;
import java.util.List;

public class Distributeur3ContratCadre extends Distributeur3Distributeur implements IAcheteurContratCadre{

    @Override
    // Implémentée par Héloise et Jeanne
    public void next() {


        super.next();
       SuperviseurVentesContratCadre superviseur = (SuperviseurVentesContratCadre) Filiere.LA_FILIERE.getActeur("Sup.CCadre");
        List<ChocolatDeMarque> listeChcocolatPertinents = new ArrayList<ChocolatDeMarque>();
        List<IActeur> transfo = new ArrayList<>();
        transfo.add(Filiere.LA_FILIERE. getActeur("EQ4"));
        transfo.add(Filiere.LA_FILIERE. getActeur("EQ5"));
        transfo.add(Filiere.LA_FILIERE. getActeur("EQ6"));
       for(ChocolatDeMarque choco :  Filiere.LA_FILIERE.getChocolatsProduits()){
           if(choco.getGamme()== Gamme.BQ){
               listeChcocolatPertinents.add(choco);
           }
       }
       
       for (IActeur a : transfo){
           if(a instanceof IVendeurContratCadre && Filiere.LA_FILIERE.getActeursSolvables().contains(a)){
               for(ChocolatDeMarque choco : listeChcocolatPertinents) {
                   if(Filiere.LA_FILIERE.getFabricantsChocolatDeMarque(choco).contains(a)) {
                       Echeancier e = new Echeancier(Filiere.LA_FILIERE.getEtape() + 1, 8, 200);
                       superviseur.demandeAcheteur(this, (IVendeurContratCadre) a, choco, e, this.cryptogramme, false);
                   }
               }
           }
       }
    }

    @Override
    public boolean achete(IProduit produit) {
        return produit instanceof ChocolatDeMarque && stockChocoMarque.containsKey(produit) && stockChocoMarque.get(produit) <= 100000;
    }

    @Override
    // Implémentée par Héloïse et Jeanne
    public Echeancier contrePropositionDeLAcheteur(ExemplaireContratCadre contrat) {
        double FourchetteHauteNego = 2000;
        double FourchetteHauteAchat = 1700;
        double FourchetteBasseNego = 1000;
        double FourchetteBasseAchat = 1500;

        /*if(contrat.getEcheancier().getNbEcheances()==8 && contrat.getQuantiteTotale()<FourchetteHauteNego && contrat.getQuantiteTotale()>FourchetteBasseNego){

        }else {
            return null;
        }
        return null;*/
        return contrat.getEcheancier();
    }

    @Override
    // Implémentée par Héloïse
    public double contrePropositionPrixAcheteur(ExemplaireContratCadre contrat){
        journalContrats.ajouter("negocie le contrat");

        int prixMoyen=0;
        if(Filiere.LA_FILIERE.getEtape()==0) {
            prixMoyen = 1500;
        }else{
            prixMoyen = (int) Filiere.LA_FILIERE.prixMoyen((ChocolatDeMarque) contrat.getProduit(), Filiere.LA_FILIERE.getEtape()-1);
        }



        //double fourchetteLimiteNegociation  = 1500;

        // a enlever, on laisse juste ça pour pouvoir négocier avec les autres
        double fourchetteLimiteNegociation  = prixMoyen;
        double fourchetteLimiteAchat = prixMoyen*0.9;
        double baisseNego = 0.5; // Correspond a 50% du prix proposé soit une baisse de 50%


        if(contrat.getPrix()<fourchetteLimiteNegociation){
            if(contrat.getPrix()<fourchetteLimiteAchat){
                return contrat.getPrix();
            }else{
                return prixMoyen*0.85;
            }
        }else{
            return -1;
        }

    }

    @Override
    // Implémentée par Jeanne
    public void notificationNouveauContratCadre(ExemplaireContratCadre contrat) {
        // System.out.println("Nouveau contrat signé pour l'équipe 9");
        this.journalActeur.ajouter("Nouveau contrat signé n°" + contrat.getNumero() + " : " + contrat.getQuantiteTotale() + " tonnes de " + contrat.getProduit() + " pour" + contrat.getPrix() + "€/tonne");
    }

    @Override
    public void receptionner(IProduit p, double quantiteEnTonnes, ExemplaireContratCadre contrat) {
        System.out.println("quantité du chocolat après recepetion : "+p.toString()+" "+this.stockChocoMarque.get(p));
        stockChocoMarque.put((ChocolatDeMarque) p,this.stockChocoMarque.get(p)+quantiteEnTonnes);
        journalActeur.ajouter("reception de "+quantiteEnTonnes+" tonnes de "+p.toString()+" du contrat "+ contrat.toString());
        this.MAJStocks();
        System.out.println("quantité du chocolat après recepetion : "+p.toString()+" "+this.stockChocoMarque.get(p));
    }
}
