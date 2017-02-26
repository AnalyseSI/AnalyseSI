/*
 * 02/25/2002 - 13:26:10
 *
 * MCD
 * Copyright (C) 2002 Dreux Loic
 * dreuxl@free.fr
 *
 * Modifications : 
 * ---------------
 *   @author : Bruno Dabo <bruno.dabo@lywoonsoftware.com>
 *   @date   : 2009 jan 22
 *   @Objet  : Cardinalité à zéro
 *   
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.analyse.merise.mcd.composant;

import java.awt.Point;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Observer;
import java.util.Set;

import org.analyse.core.gui.zgraph.ZElement;
import org.analyse.core.gui.zgraph.ZGraphique;
import org.analyse.core.gui.zgraph.ZLien;
import org.analyse.core.util.GUIUtilities;
import org.analyse.core.util.Utilities;
import org.analyse.core.util.Constantes;
import org.analyse.merise.gui.table.DictionnaireTable;

/**
 * Composant Graphique représentant un MCD.
 */
public class MCDComponent extends ZGraphique implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -630088766787499921L;


	/** dictionnaire des informations */
	private DictionnaireTable data;

	private String msg;

	/**
	 * Créer un nouveau <code>MCDComponent</code>
	 * 
	 * @param data
	 *            Dictionnaire des informations à associer au MCD.
	 */
	public MCDComponent(DictionnaireTable data) {
		super();
		this.data = data;

		data.addObserver(this);
	}

	/**
	 * Ajoute un MCDObjet dans le MCD.
	 * 
	 * Utilisé lors d'un chargement d'un fichier
	 */
	public void addObjet(MCDObjet obj) {
		data.addObserver(obj);
		super.addElement(obj);
	}

	/**
	 * Ajoute une nouvelle <code>MCDEntite</code> vide.
	 * 
	 * Utilisé lors de la création d'une nouvelle entité
	 */
	public MCDEntite addEntite(int x, int y) {
		MCDEntite ent ;

		//bug : 347422 
		ent = new MCDEntite(this, x, y) ; 
		
		super.addElement(ent);
		
		data.addObserver(ent);
		return ent;
	}

	/**
	 * Ajoute une nouvelle <code>MCDAssociation</code> vide.
	 * 
	 * Utilisé lors de la création d'une nouvelle association
	 */
	public MCDAssociation addAssociation(int x, int y) {
		MCDAssociation ass;
		super.addElement(ass = new MCDAssociation(this, x, y));
		data.addObserver(ass);
		return ass;
	}

	/**
	 * Appelle la méthode creerLien, cette méthode permet à l'utilisateur de
	 * sélectionner deux <code>MCDObjet</code> afin de les lier.
	 * 
	 * Utilisé lors de la création d'un nouveau lien
	 */
	public void addLien() {
		creerLien(new MCDLien());
	}

	/**
	 * Supprime un lien ainsi que leurs références dans les
	 * <code>MCDObjet</code>.
	 */
	public void removeLien(ZLien lien) {
		MCDObjet elem1 = (MCDObjet) (lien.getElement(Constantes.MCDENTITE1));
		elem1.delLink((MCDLien) lien);
		MCDObjet elem2 = (MCDObjet) (lien.getElement(Constantes.MCDENTITE2));
		elem2.delLink((MCDLien) lien);
		super.removeLien(lien);
	}

	/**
	 * Supprime le lien sélectionné
	 */
	public void removeLien() {
		removeLien(lienClic);
	}

	/**
	 * Supprime le <code>MCDObjet</code>. Cette méthode s'occupe de libérer
	 * les informations que contenait le MCDObjet.
	 */
	public void removeObjet(ZElement element) {
		MCDObjet obj = (MCDObjet) element;
		obj.clearInformations();
		super.removeElement(element);
	}

	/**
	 * Supprime le <code>MCDObjet</code> sélectionné.
	 */
	public MCDObjet removeObjet() {
		ZElement element = elementClic;
		this.removeObjet(elementClic);
		return (MCDObjet) element;
	}

    /**
     * Supprime les {@link MCDObjet}s sélectionnés.
     */
    public Set<MCDObjet> removeObjets() {
        Set<ZElement> selectionAvantEffacement = new LinkedHashSet<ZElement>(selectionCourante);
        Set<MCDObjet> observersAEffacer = new LinkedHashSet<MCDObjet>();
        for (ZElement element : selectionAvantEffacement) {
            removeObjet(element);
            if (element instanceof MCDObjet)
                observersAEffacer.add((MCDObjet) element);
        }
        return observersAEffacer;
    }

	/**
	 *  
	 */
	public void clear() {
		super.clearAll();
	}

	/**
	 * Retourne le <code>MCDObjet</code> correspondant au nom passé en
	 * paramètre.
	 */
	public MCDObjet getElement(String name) {
		for (Iterator<ZElement> e = enumElements(); e.hasNext();) {
			MCDObjet o = (MCDObjet) e.next();
			if (o.getName().equals(name))
				return o;
		}
		return null;
	}

	/**
	 * Indique s'il est possible de créer un lien entre les deux éléments. <br>
	 * Retourne vrai si les deux éléments représentent une association et une
	 * entité, faux dans le cas contraire.
	 */
    public boolean peutCreerLien(ZElement elem1, ZElement elem2) {
        return super.peutCreerLien(elem1, elem2)
                && (elem1 instanceof MCDAssociation
                && elem2 instanceof MCDEntite || elem2 instanceof MCDAssociation
                && elem1 instanceof MCDEntite);
    }

	/**
	 * Retourne la table du dictionnaire des informations correspondant au MCD.
	 */
	public DictionnaireTable getData() {
		return data;
	}

	/**
	 * Retourne vrai si le MCD est correct.
	 */
	public boolean isCorrect(int showErrors) {
		MCDObjet obj;
		boolean correct = true;
		boolean dicoCorrect = true;
		msg = "";

		// Vérification du dictionnaire
		if (!data.allUse())
			msg += "<b style=\"color: blue;\"><br/>/!\\Attention</b><br>" + 
			 Utilities.getLangueMessage ("information_non_utilisee") + "<br>";

		for (int i = 0; i < data.getRowCount(); i++) {
			if(!data.verifySize(i))
			{
				dicoCorrect = false;
				msg += "<b style=\"color: red;\"><br/>/!\\Attention</b><br>La taille de l'information "
						+ data.getValueAt(i, DictionnaireTable.NAME)
						+ " est incorrecte.<br>";
			}
		}

		if (dicoCorrect) {
			msg += Utilities.getLangueMessage ("dictionnaire_correct") + "<br>";
		} else {
			msg += Utilities.getLangueMessage ("dictionnaire_erreur") + "<br>";
		}
		//-----------------------------

		if (dicoCorrect) {
			for (Iterator<ZElement> e = this.elementsZElements(); e.hasNext();) {
				obj = (MCDObjet) e.next();

				if (obj instanceof MCDAssociation) {
					if (!correct)
						associationCorrect((MCDAssociation) obj);
					else
						correct = associationCorrect((MCDAssociation) obj);
				} else if (obj instanceof MCDEntite) {
					if (obj.sizeInformation() < 1) {
						correct = false;
						msg += "<b style=\"color: red;\"><br/>/!\\</b> L'entité \""
								+ obj.getName()
								+ "\" n'a pas d'identifiant ...<br>";
					}
				}
			}
		} else {
			correct = false;
		}

		if (correct && (showErrors == Constantes.SHOW_ALL)) {
			GUIUtilities.messageHTML(msg
					+ "<b style=\"color: blue;\">MCD correct ...</b>", true, 300, 250);
		} else if ((!correct)
				&& (showErrors == Constantes.SHOW_ERRORS || showErrors == Constantes.SHOW_ALL)) {
			GUIUtilities.messageHTML(msg
					+ "<b style=\"color: red;\">MCD INCORRECT ...</b>", false);
		} else if (correct && showErrors == Constantes.CREATE_MCD) {
			GUIUtilities.messageHTML(msg
					+ "<b style=\"color: blue;\">" + Utilities.getLangueMessage ("mpd_ok") + "<br>"
					+ Utilities.getLangueMessage ("mldr_ok") + "<br>"
					+ Utilities.getLangueMessage ("sql_ok") + "<br>",
					true, 300, 250);
		} else if (!correct && showErrors == Constantes.CREATE_MCD) {
			GUIUtilities
					.messageHTML(msg
							+ "<b style=\"color: red;\">" + Utilities.getLangueMessage ("mcd_incorrect") 
							+ "</b>", false);
		}

		return correct;
	}

	/**
	 * Vérifie les cardinalitées des associations.
	 */
	private boolean associationCorrect(MCDAssociation ass) {

		// L'association à plus de 2 liens
		// ou l'association est une association porteuse d'informations
		if (ass.sizeLink() > 2 || ass.sizeInformation() > 0) {
			// Vérifie que l'association a au moins deux liens dans
			// le cas de la porteuse.
			if (ass.sizeLink() < 2) {
				msg += "<b style=\"color: red;\">/!\\</b> Le nombre de liens de l'association \""
						+ ass.getName() + "\" est inférieur à deux.<br>";
				return false;
			}
		
		} /* else if (ass.sizeLink() < 2) {
			msg += "<b style=\"color: red;\">/!\\</b> Le nombre de liens de l'association \""
					+ ass.getName() + "\" est inférieur à deux.<br>";
			return false;
		} */

		return true;
	}

	/**
	 * Retourne vrai lorsque une nouvelle table va etre crée à partir de
	 * l'association.
	 */
	private boolean newTable(MCDAssociation ass) {
		if (ass.sizeLink() > 2 || ass.sizeInformation() > 0)
			return true;

		boolean porteuse = true;
		for (Iterator<MCDLien> e = ass.links(); e.hasNext();)
			if (!e.next().getCardMax().equals("N"))
				porteuse = false;

		return porteuse;
	}

	/**
	 * Retourne le type de l'association
	 */
	private String typeAssociation(MCDAssociation ass) {
		
		//@TODO private String typeAssociation(MCDAssociation ass) :  ce bout de code laisse à désirer - le modifier rapidement
		 /*
		  * * -> la fonction donne plusieurs résultats possibles à analyser par la fonction appelante. C'est source à quelques ennuis 
		  */
		 
		
		if (ass.sizeLink() > 2 || ass.sizeInformation() > 0)
			return Constantes.PORTEUSE;

		boolean porteuse = true;
		boolean un_un = true;
		boolean zero_n = false ; 
		boolean zero_un = false ;
		
		for (Iterator<MCDLien> e = ass.links(); e.hasNext();) {
			MCDLien lien = e.next();
			
			if (!lien.getCardMax().equals("N"))
				porteuse = false;
			
			if (!lien.getCardMax().equals("1"))
				un_un = false;
			
			if ( lien.getCardMin().equals("0") ) {
				if ( ! lien.getCardMax().equals("1") ) 
					zero_n = true ;
				else 
					zero_un = true ;
			}
			
		}

		if (porteuse)
			return Constantes.PORTEUSE;  // a vérifier 

		if (zero_un)
			return Constantes.ZERO_UN;
		
		if (un_un)
			return Constantes.UN_UN;
		
		if ( zero_n )
			return Constantes.ZERO_N;
		
		return Constantes.UN_N;
	}

	/**
	 * Retourne l'objet <code>MCDEntite</code> lié à l'association
	 * @author bruno.dabo@lywoonsoftware.com
	 * @param ass association
	 *            
	 * 
	 */
	private MCDEntite getMCDEntite( MCDAssociation ass )  {
		for (Iterator<ZLien> e = elementsZLiens(); e.hasNext();) {
			MCDLien lien = (MCDLien) (e.next());

			if (lien.getElement(  Constantes.MCDENTITE1 ).equals(ass) )
				return (MCDEntite) (lien.getElement(  Constantes.MCDENTITE2 ));
		}
		return null;
	}
	
	/**
	 * Retourne l'objet <code>MCDEntite</code> lié à l'association
	 * 
	 * @param ass
	 *            association
	 * @param card
	 *            Cardinalité
	 */
	private MCDEntite getMCDEntite(MCDAssociation ass, String card) {
		for (Iterator<ZLien> e = elementsZLiens(); e.hasNext();) {
			MCDLien lien = (MCDLien) (e.next());

			if (lien.getElement(  Constantes.MCDENTITE1 ).equals(ass)
					&& lien.getCardMax().equals(card))
				return (MCDEntite) (lien.getElement( Constantes.MCDENTITE2 ));
		}
		return null;
	}
	

	/**
	 * Retourne l'objet <code>MCDEntite</code> lié à l'association
	 * 
	 * @param ass
	 *            association
	 * @param num
	 *            index
	 */
	private MCDEntite getMCDEntite(MCDAssociation ass, int num) {
		int tmp = 0;
		for (Iterator<ZLien> e = elementsZLiens(); e.hasNext();) {
			MCDLien lien = (MCDLien) (e.next());

			if (lien.getElement ( Constantes.MCDENTITE1 ).equals(ass) && tmp == num)
				return (MCDEntite) (lien.getElement ( Constantes.MCDENTITE2 ));
			else if (lien.getElement( Constantes.MCDENTITE1 ).equals(ass))
				tmp++;
		}
		return null;
	}

	/**
	 * Créer le MPD correspondant au MCD.
	 */
	public boolean buildMPD(MPDComponent mpd, int showErrors) {
		MPDEntite ent = null , ent2 = null ;
		MCDObjet obj;
		MPDLien mpdLien;
		MCDLien mcdLienAssociation  = null ; 
		
		if (!this.isCorrect(showErrors))
			return false;

		mpd.clearAll();
		mpd.setWidth(this.getPreferredSize().getWidth());

		/*
		 * Création des MPDEntite à partir des MCDEntite et des MCDAssociation
		 */
		
		for (Iterator<ZElement> e = this.elementsZElements(); e.hasNext();) {
			obj = (MCDObjet) e.next();

			if (obj instanceof MCDEntite) {
				ent = new MPDEntite(mpd, Utilities.normaliseString(obj.getName(), Constantes.LOWER));
				ent.setPosition(new Point(obj.getX(), obj.getY()));
				ent.addIdentifiant(obj.getCodeInformation(0));
				ent.addInformations(obj.getInformations());
				mpd.addMPDEntite(ent);
			}
		}
		
		for (Iterator<ZElement> e = this.elementsZElements(); e.hasNext();) {
			obj = (MCDObjet) e.next();
			
			if (obj instanceof MCDAssociation) {
				
				String ws_typeAssociation = typeAssociation ((MCDAssociation) obj) ; 
				
				
				if ( ws_typeAssociation.equals ( Constantes.PORTEUSE) ) {
					ent = new MPDEntite(mpd, Utilities.normaliseString(obj.getName(), Constantes.LOWER));
					
					ent.setPosition(new Point(obj.getX(), obj.getY()));
					
					List<MCDLien> vLinks = ( (MCDAssociation) obj ).links ;  // Les liens de l'association sont dans un vecteur
	
					
					for ( int k=0; k < vLinks.size() ; k++ ) {
						// récupération des différents liens de l'association 
						mcdLienAssociation = vLinks.get( k );
						
						MCDEntite mcdEntite = ( MCDEntite ) mcdLienAssociation.getElement ( Constantes.MCDENTITE2 ) ;
						
						ent2 = mpd.getMPDEntite(Utilities.normaliseString (mcdEntite.getName() , Constantes.LOWER));
						if ( ent2 == null ) 
							ent2 = new MPDEntite(mpd, Utilities.normaliseString(mcdEntite.getName(), Constantes.LOWER));						
							
						mpdLien = new MPDLien();
							//ent2 = mpd.getMPDEntite(Utilities.normaliseString(mcdLienAssociation.getMCDObjet ( Constantes.MCDENTITE2 ).getName(), Constantes.LOWER));
						//ent.addForeignKey(ent2.getCodeInformation(0), Utilities.normaliseString( ent2.getName(), Constantes.LOWER) ) ;  
						mpdLien.setElement(ent, Constantes.MCDENTITE2);
						
						//ent2.addForeignKey(ent.getCodeInformation(0), Utilities.normaliseString( ent.getName(), Constantes.LOWER) ) ;  
						mpdLien.setElement(ent2, Constantes.MCDENTITE1);

						mpd.addLien(mpdLien);
 
						ent.addIdentifiant(mcdLienAssociation.getMCDObjet ( Constantes.MCDENTITE2 ).getCodeInformation(0));
						ent.addForeignKey(ent2.getCodeInformation(0), Utilities.normaliseString( ent2.getName(), Constantes.LOWER) ) ;						
					}
					
					ent.addInformations(obj.getInformations());   // les propriétés de la relation
					
					if ( vLinks.size() == 1 ) { //liaison reflexive
						
						MCDAssociation mCDAssociation = ( MCDAssociation ) vLinks.get(0).getElement ( Constantes.MCDENTITE1 ) ;
						String str = Utilities.normaliseString(ent2.getCodeInformation(0),  Constantes.LOWER) + "_" + Utilities.normaliseString(mCDAssociation.getName() , Constantes.LOWER) ;						 
						if ( mcdLienAssociation.getCardMin().equals( "1" ) ) {
							ent.addIdentifiant ( str ) ;
						} else {
							ent.addInformation ( str ) ;
						}
					} 
                    
					
					mpd.addMPDEntite(ent);
					
				} 
				
				
				if ( ws_typeAssociation.equals( Constantes.UN_UN ) || ws_typeAssociation.equals( Constantes.ZERO_UN ) ) {									                       
					
					mpdLien = new MPDLien();
					int numEntite = -1; 
					
					List<MCDLien> vLinks = ( (MCDAssociation) obj ).links ;  // Les liens de l'association sont dans un vecteur 
					boolean traitement = false ; 
					int k = 0 ; 
					MCDEntite mcdEntiteAssociee = null ; 
					
					for ( k=0; k < vLinks.size() ; k++ ) {
						
						// récupération des différents liens de l'association 
						mcdLienAssociation = vLinks.get( k );
						 
						MCDEntite mcdEntite = ( MCDEntite ) mcdLienAssociation.getElement ( Constantes.MCDENTITE2 ) ;
						
						ent = mpd.getMPDEntite(Utilities.normaliseString (mcdEntite.getName() , Constantes.LOWER));
						ent.setForeignKeyCanBeNull ( true ) ;
						
						// #693487 if ( mcdLienAssociation.getCardMin().equals( "1" )  && mcdLienAssociation.getCardMax().equals( "1" )  ) { 
						if ( mcdLienAssociation.getCardMax().equals( "1" )  ) {  // #693487
							traitement = true  ;
							// récupération de la primary key de l'entité associée pour la passer en foreign key dans l'entité courante
							numEntite = ( k == 0 ) ? 1 : 0 ; 
														
							if ( vLinks.size () == 1 ) numEntite = 0 ;   //liaison reflexive
							
							MCDLien ws_lien = vLinks.get( numEntite );
							
							mcdEntiteAssociee = ( MCDEntite )  ws_lien.getElement( Constantes.MCDENTITE2 )  ; 
							//String nameEntiteAssociee = mcdEntiteAssociee.getName() ;
							
							if ( vLinks.size() == 1 ) { //liaison reflexive
								MCDAssociation mCDAssociation = ( MCDAssociation ) vLinks.get(0).getElement ( Constantes.MCDENTITE1 ) ;
								ent.addIdentifiant ( mcdEntiteAssociee.getCodeInformation(0) + "_" + Utilities.normaliseString(mCDAssociation.getName() , Constantes.LOWER)  ) ;
									
							} else {

                                /*
                                 * Traiter le cas des nom d'attributs utilisés plusieurs fois
                                 */
                                String nomTable = Utilities.normaliseString( mcdEntiteAssociee.getName(), Constantes.LOWER) ;
                                String ws_foreignkey = ( nomTable+"_"+ mcdEntiteAssociee.getCodeInformation(0) ).toLowerCase() ;

                                ent.addForeignKey(ws_foreignkey, nomTable) ;  // Bug #520410
                                if ( ws_typeAssociation.equals( Constantes.ZERO_UN ) )
                                	ent.setForeignKeyCanBeNull ( true ) ;
                                else 
									if ( ws_lien.getCardMin().equals("0")) {				
										ent.setForeignKeyCanBeNull ( true ) ;
									} else
										ent.setForeignKeyCanBeNull ( false ) ;

								
								ent.addInformation(ws_foreignkey) ;
								
								if (ws_typeAssociation.equals( Constantes.ZERO_N ) )
									return true ;
							}							
						} 
						
						if ( k < 1 )
							mpdLien.setElement(ent, Constantes.MCDENTITE2);
						else 
							mpdLien.setElement(ent, Constantes.MCDENTITE1);
					
					}
					
					if ( vLinks.size () == 1 )  {  //liaison reflexive
						
						if ( ws_typeAssociation.equals( Constantes.ZERO_UN ) ) {  
							MCDAssociation mCDAssociation = ( MCDAssociation ) vLinks.get(0).getElement ( Constantes.MCDENTITE1 ) ;
							MCDLien ws_lien = vLinks.get( 0 );
							mcdEntiteAssociee = ( MCDEntite )  ws_lien.getElement( Constantes.MCDENTITE2 )  ;							
							ent.addInformation( mcdEntiteAssociee.getCodeInformation(0) + "_" + Utilities.normaliseString(mCDAssociation.getName() , Constantes.LOWER) ) ;
						}
						
					} else {
						
						if ( ! traitement ) {
							// cas des relations ( 0, 1 ) - ( 0 , 1 ) 
							
							//traitement de la "1ere" entite
							MCDLien ws_lien = vLinks.get( 0 );
							MCDEntite mcdEntite = ( MCDEntite )  ws_lien.getElement( Constantes.MCDENTITE2 )  ;
							String nameEntite = mcdEntite.getName() ;
								
							MCDLien ws_lienAssocie = vLinks.get( 1 );
							mcdEntiteAssociee = ( MCDEntite )  ws_lienAssocie.getElement( Constantes.MCDENTITE2 )  ;
								
							ent = mpd.getMPDEntite(Utilities.normaliseString (nameEntite , Constantes.LOWER));

                                                        /*
                                                         * Bug #520410 - voir test-007.asi
							ent.addForeignKey(mcdEntiteAssociee.getCodeInformation(0), Utilities.normaliseString( mcdEntiteAssociee.getName(), Constantes.LOWER) ) ;
							ent.addInformation( mcdEntiteAssociee.getCodeInformation(0) ) ;
							*/

							mpdLien.setElement(ent, Constantes.MCDENTITE1);												
							
							//traitement de la "2eme" entite
							
							numEntite = 1 ;
							ws_lien = vLinks.get( 1 );
							mcdEntite = ( MCDEntite )  ws_lien.getElement( Constantes.MCDENTITE2 )  ;
							nameEntite = mcdEntite.getName() ;
							
							ws_lienAssocie = vLinks.get( 0 );
							mcdEntiteAssociee = ( MCDEntite )  ws_lienAssocie.getElement( Constantes.MCDENTITE2 )  ;
							
							ent = mpd.getMPDEntite(Utilities.normaliseString (nameEntite , Constantes.LOWER));
							
							ent.addForeignKey(mcdEntiteAssociee.getCodeInformation(0), Utilities.normaliseString( mcdEntiteAssociee.getName(), Constantes.LOWER) ) ;
							ent.addInformation( mcdEntiteAssociee.getCodeInformation(0)) ;
							
							mpdLien.setElement(ent, Constantes.MCDENTITE2 );
						}
					}
					
				} 
				
				if ( ws_typeAssociation.equals ( Constantes.UN_N ) ||  ws_typeAssociation.equals ( Constantes.ZERO_N ) ) {
				
					mpdLien = new MPDLien();
					List<MCDLien> vLinks = ( (MCDAssociation) obj ).links ;  // Les liens de l'association sont dans un vecteur 
					
					for ( int k=0; k < vLinks.size() ; k++ ) {
						// récupération des différents liens de l'association 
						mcdLienAssociation = vLinks.get( k );
						 
						MCDEntite mcdEntite = ( MCDEntite ) mcdLienAssociation.getElement ( Constantes.MCDENTITE2 ) ;
						
						ent = mpd.getMPDEntite(Utilities.normaliseString (mcdEntite.getName() , Constantes.LOWER));
						
						if ( mcdLienAssociation.getCardMin().equals( "0" ) ) 
							ent.setForeignKeyCanBeNull ( true ) ;
						else 
							ent.setForeignKeyCanBeNull ( false ) ;
						
						if ( ! mcdLienAssociation.getCardMax().equals( "N" ) ) {
						
							// traitement des relations ternaires
							
							if ( isPartOfTernaire ( mcdLienAssociation ) )
								ent.addInformation(getMCDEntite((MCDAssociation) obj, "N").getCodeInformation(0)+ "_" +
										Utilities.normaliseString(obj.getName() , Constantes.LOWER) );
							else  {
								if ( ! ent.existInformation(getMCDEntite((MCDAssociation) obj, "N").getCodeInformation(0) )) { 
									ent.addInformation(getMCDEntite((MCDAssociation) obj, "N").getCodeInformation(0));
									ent.addForeignKey(getMCDEntite((MCDAssociation) obj, "N").getCodeInformation(0) ,
										Utilities.normaliseString(getMCDEntite((MCDAssociation) obj, "N").getName(), Constantes.LOWER));
								} else {
									
									ent.addInformation(getMCDEntite((MCDAssociation) obj, "N").getCodeInformation(0)+ "_" +
											Utilities.normaliseString(obj.getName() , Constantes.LOWER) );
									

                                    ent.addForeignKey(getMCDEntite((MCDAssociation) obj, "N").getCodeInformation(0) + "_" +
									Utilities.normaliseString( obj.getName(), Constantes.LOWER)  ,
									Utilities.normaliseString(getMCDEntite((MCDAssociation) obj, "N").getName(), Constantes.LOWER));
                                     
								}
							}
				
							if ( ! ws_typeAssociation.equals ( Constantes.ZERO_N ) ) {	
									
								if ( isPartOfTernaire ( mcdLienAssociation ) ) {
									
									MCDAssociation mCDAssociation = null ;
									if ( vLinks.size() == 1 ) { //liaison reflexive
										mCDAssociation = ( MCDAssociation ) vLinks.get(0).getElement ( Constantes.MCDENTITE2 ) ;
										ent.addInformation( mcdEntite.getCodeInformation(0) + "_" + Utilities.normaliseString(mCDAssociation.getName() , Constantes.LOWER) ) ;
									}

                                            ent.addForeignKey(getMCDEntite((MCDAssociation) obj, "N").getCodeInformation(0) + "_" +
											Utilities.normaliseString( obj.getName(), Constantes.LOWER)  ,
											Utilities.normaliseString(getMCDEntite((MCDAssociation) obj, "N").getName(), Constantes.LOWER));


								} else 				
									ent.addForeignKey(getMCDEntite((MCDAssociation) obj, "N").getCodeInformation(0),
										Utilities.normaliseString(getMCDEntite((MCDAssociation) obj, "N").getName(), Constantes.LOWER));
							}
							
						} else {

							MCDAssociation mCDAssociation = null ;
							if ( vLinks.size() == 1 ) { //liaison reflexive
								mCDAssociation = ( MCDAssociation ) vLinks.get(0).getElement ( Constantes.MCDENTITE2 ) ;
								ent.addInformation( mcdEntite.getCodeInformation(0) + "_" + Utilities.normaliseString(mCDAssociation.getName() , Constantes.LOWER) ) ;
							} 
						}
						
						mpdLien.setElement(ent, Constantes.MCDENTITE2);
					
					}

					ent = mpd.getMPDEntite(Utilities.normaliseString(
							getMCDEntite((MCDAssociation) obj, "N").getName(),
							Constantes.LOWER));
					mpdLien.setElement(ent, Constantes.MCDENTITE1 );

					mpd.addLien(mpdLien);
				}
			}
		}

		/*
		 * Suppression des MPDEntite ne contenant qu'un identifiant
		 */
		/*
		 * Vector elementsToDel = new Vector(); for (Enumeration e =
		 * mpd.elementsZElements(); e.hasMoreElements();) { ent = (MPDEntite)
		 * e.nextElement(); if (ent.sizeInformation() == 1 &&
		 * ent.sizeIdentifiant() == 1) {
		 * 
		 * for (Enumeration e2 = mpd.elementsZElements(); e .hasMoreElements();) {
		 * ent2 = (MPDEntite) e.nextElement();
		 * ent2.removeForeignKey(ent.getCodeInformation(0)); }
		 * elementsToDel.add(ent); } } for (Enumeration e =
		 * elementsToDel.elements(); e.hasMoreElements();) {
		 * mpd.removeElement((ZElement) e.nextElement()); }
		 */
		return true;
	}
	

	
	private Boolean isPartOfTernaire ( MCDLien mcdLien )  {
		
		// la cardinalité est 1,1 sur ce lien
		
		Boolean ternaire = false  ;
		
		MCDAssociation association = ( MCDAssociation ) mcdLien.getElement ( Constantes.MCDENTITE1 ) ;
		MCDLien entiteLink = null, ws_lien = null  ;
		String v0_name, v1_name, entite0_name = null , entite1_name = null ;
		MCDObjet obj ; 
		List<MCDLien> vLinks = null, vLinks_rech = null ; 
		
		// on récupère l'entité associée 
		vLinks = association.links ;
		for ( int k=0; k < vLinks.size() ; k++ ) 
			if ( ! vLinks.get( k ).equals( mcdLien ) ) {
				entiteLink = vLinks.get( k );
				entite0_name = (( MCDEntite )  mcdLien.getElement( Constantes.MCDENTITE2 )).getName ()  ; 
				entite1_name = (( MCDEntite )  entiteLink.getElement( Constantes.MCDENTITE2 )).getName ()  ; 
				break ;
			}
		
		// on cherche une autre association reliant les 2 entités
		for (Iterator<ZElement> e = this.elementsZElements(); e.hasNext();) {
			obj = (MCDObjet) e.next();
				
			if (obj instanceof MCDAssociation) {
				vLinks_rech = ( (MCDAssociation) obj ).links ;  // Les liens de l'association sont dans un vecteur 
				
				if ( ! obj.equals( association ) ) {
					ws_lien = vLinks_rech.get( 0 );
					v0_name = (( MCDEntite )  ws_lien.getElement( Constantes.MCDENTITE2 )).getName ()  ;
					ws_lien = vLinks.get( 1 );
					v1_name = (( MCDEntite )  ws_lien.getElement( Constantes.MCDENTITE2 )).getName ()  ;
					
					if ( ( v0_name.equals ( entite0_name ) &&  v1_name.equals ( entite1_name ) ) || 
						( v0_name.equals ( entite1_name ) &&  v1_name.equals ( entite0_name ) ) ) {
						ternaire = true ; 
						break ;
					}
				}
				
			}
		}
		
		ternaire = false ; // je sais ... un bug en suspens pour ceux qui ont suivi le pb des relations ternaires
		return ternaire  ; 
	}
}
