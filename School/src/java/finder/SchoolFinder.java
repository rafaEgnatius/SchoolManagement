/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package finder;

import entity.AbstractEntity;
import entity.Ankieta;
import entity.Faktura;
import entity.Firma;
import entity.Jezyk;
import entity.JezykLektora;
import entity.Kurs;
import entity.Kursant;
import entity.Lektor;
import entity.Podrecznik;
import entity.Rachunek;
import entity.Test;
import entity.Wplata;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import sorter.FieldSorter;

/**
 *
 * @author Rafa
 */
public class SchoolFinder {

    // find by String:
    public static List findCourse(List kursList, String searchPhrase) {
        //        for every field in Lektor entity check whether it contains searchPhrase
        //        use toLower to find everything no matter wheter capital letters were used   
        List resultList = new ArrayList<>(); // to send back
        Iterator it = kursList.iterator();
        while (it.hasNext()) {
            Kurs kurs = (Kurs) it.next();
            if (kurs.getSymbol().toLowerCase().contains(searchPhrase.toLowerCase())
                    || (kurs.getFirma() != null && kurs.getFirma().getNazwa().toLowerCase().contains(searchPhrase.toLowerCase()))
                    || (kurs.getLektor() != null && kurs.getLektor().getNazwa().toLowerCase().contains(searchPhrase.toLowerCase()))
                    || kurs.getJezyk().getNazwa().toLowerCase().contains(searchPhrase.toLowerCase())) {
                resultList.add(kurs);
            }
        }
        return resultList;
    }

    public static List findLanguageTest(List mainEntityList, String searchPhrase) {
        List resultList = new ArrayList<>(); // to send back

        Iterator it = mainEntityList.iterator();
        while (it.hasNext()) {
            Test test = (Test) it.next();
            if (test.getRodzaj().toLowerCase().contains(searchPhrase.toLowerCase())
                    || test.getKurs().getSymbol().toLowerCase().contains(searchPhrase.toLowerCase())
                    || test.getKurs().getFirma().getNazwa().toLowerCase().contains(searchPhrase.toLowerCase())
                    || test.getKursant().getNazwa().toLowerCase().contains(searchPhrase.toLowerCase())) {
                resultList.add(test);
            }
        }

        return resultList;
    }

    // find by String:
    public static List<Lektor> findLector(List<Lektor> lektorList, String searchPhrase) {
        List resultList = new ArrayList<>(); // to send back
//        for every field in Lektor entity check whether it contains searchPhrase
//        use toLower to find everything no matter wheter capital
        lektorList.stream().filter((lektor) -> (lektor.getNazwa().toLowerCase().contains(searchPhrase.toLowerCase())
                || lektor.getEmail().toLowerCase().contains(searchPhrase.toLowerCase())
                || lektor.getMiasto().toLowerCase().contains(searchPhrase.toLowerCase())
                || lektor.getTelefon().toLowerCase().contains(searchPhrase.toLowerCase()))).forEach((lektor) -> {
                    resultList.add(lektor);
                });
        return resultList;
    }

    public static List findCustomerForLanguageTest(List mainEntityList, List kursList, String searchOption) {
        List resultList = new ArrayList<>(); // to send back
//        for every entity
//        find from second list

        // search for courses (kurs Entity) with matching customer (firma)
        Iterator it2 = kursList.iterator();
        // first loop -> select only those that equal searchOption
        while (it2.hasNext()) {
            Kurs kurs = (Kurs) it2.next();
            Firma firma = kurs.getFirma();
            if (firma != null && kurs.getFirma().getId().toString().equals(searchOption)) {

                // second loop -> select only those that have this course (kurs)
                Iterator it = mainEntityList.iterator();
                while (it.hasNext()) {
                    Test test = (Test) it.next();
                    if (test.getKurs().equals(kurs)) {
                        resultList.add(test);
                    }
                }
            }
        }

        return resultList;
    }

    public static List findCustomerForParticipant(List mainEntityList, List firmaList, String searchOption) {
        List resultList = new ArrayList<>(); // to send back
//        for every entity
//        find from second list

        Iterator it2 = firmaList.iterator();
        // first loop -> select only those that equal searchOption
        while (it2.hasNext()) {
            Firma firma = (Firma) it2.next();
            if (firma.getId().toString().equals(searchOption)) {

                // second loop -> select only those that equal firma
                Iterator it = mainEntityList.iterator();
                while (it.hasNext()) {
                    Kursant kursant = (Kursant) it.next();
                    if (kursant.getFirma().equals(firma)) {
                        resultList.add(kursant);
                    }
                }

            }
        }

        return resultList;
    }

    public static List findFaktura(List fakturaList, List firmaList, String searchPhrase) {
        List resultList = new ArrayList<>(); // to send back

//        for every field in entity check whether it contains searchPhrase
//        use toLower to find everything no matter wheter capital      
        Iterator it = fakturaList.iterator();
        while (it.hasNext()) {
            Faktura faktura = (Faktura) it.next();
            if (faktura.getNumer().toLowerCase().contains(searchPhrase.toLowerCase())
                    || faktura.getOpis().toLowerCase().contains(searchPhrase.toLowerCase())
                    || faktura.getFirma().getNazwa().toLowerCase().contains(searchPhrase.toLowerCase())) {
                resultList.add(faktura);
            }
        }

        return resultList;
    }

    public static List findParticipant(List mainEntityList, String searchPhrase) {
        List resultList = new ArrayList<>(); // to send back
        for (Iterator it = mainEntityList.iterator(); it.hasNext();) {
            Kursant mainEntity = (Kursant) it.next();
            if (mainEntity.getNazwa().toLowerCase().contains(searchPhrase.toLowerCase())
                    || mainEntity.getEmail().toLowerCase().contains(searchPhrase.toLowerCase())
                    || mainEntity.getTelefon().toLowerCase().contains(searchPhrase.toLowerCase())) {
                resultList.add(mainEntity);
            }
        }
        return resultList;
    }

    // find by String
    public static List findLanguage(List<Jezyk> jezykList, String searchPhrase) {
        List resultList = new ArrayList<>(); // to send back
//        for every field in entity check whether it contains searchPhrase
//        use toLower to find everything no matter wheter capital
        jezykList.stream().filter((jezyk) -> (jezyk.getNazwa().toLowerCase().contains(searchPhrase.toLowerCase())
                || jezyk.getSymbol().toLowerCase().contains(searchPhrase.toLowerCase()))).forEach((jezyk) -> {
                    resultList.add(jezyk);
                });
        return resultList;
    }

    public static List findLanguageForCourse(List kursList, String searchOption) {

        //        for every entity
        //        find what option is needed
        List resultList = new ArrayList<>(); // to send back

        for (Iterator it = kursList.iterator(); it.hasNext();) {
            Kurs kurs = (Kurs) it.next();
            if (kurs.getJezyk().getId().toString().equals(searchOption)) {
                resultList.add(kurs);
            }
        }
        return resultList;
    }

    // find by option:
    public static List<Lektor> findLanguageForLector(List<Lektor> lektorList, List<JezykLektora> jezykLektoraList, String searchOption) {
        List resultList = new ArrayList<>(); // to send back
//        for every entity
//        find what languages he speaks
//        taking it from language lector list (jezykLektora)
//        check if this language is the same as searchLanguage
        lektorList.stream().forEach((lektor) -> {
            jezykLektoraList.stream().filter((jezykLektora) -> (lektor.equals(jezykLektora.getLektor()))).filter((jezykLektora) -> (jezykLektora.getJezyk().getId().toString().equals(searchOption))).forEach((_item) -> {
                resultList.add(lektor);
            });
        });
        return resultList;
    }

    // find by option:
    public static List findLanguageForTextbook(List podrecznikList, String searchOption) {
        List resultList = new ArrayList<>(); // to send back
//        for every entity
//        check if its language equals with search option that was chosen
        for (Iterator it = podrecznikList.iterator(); it.hasNext();) {
            Podrecznik podrecznik = (Podrecznik) it.next();
            if (podrecznik.getJezyk().getId().toString().equals(searchOption)) {
                resultList.add(podrecznik);
            }
        }
        return resultList;
    }

    // find by String:
    public static List findTextbook(List podrecznikList, String searchPhrase) {
//        for every entity

        List resultList = new ArrayList<>(); // to send back
        for (Iterator it = podrecznikList.iterator(); it.hasNext();) {
            Podrecznik podrecznik = (Podrecznik) it.next();
            if (podrecznik.getNazwa().toLowerCase().contains(searchPhrase.toLowerCase())
                    || podrecznik.getPoziom().toLowerCase().contains(searchPhrase.toLowerCase())) {
                resultList.add(podrecznik);
            }
        }
        return resultList;
    }

    /**
     *
     * @param mainEntityList
     * @param searchPhrase
     * @return
     */
    public static List findQuestionnaire(List mainEntityList, String searchPhrase) {
        List resultList = new ArrayList<>(); // to send back
        for (Iterator it = mainEntityList.iterator(); it.hasNext();) {
            Ankieta ankieta = (Ankieta) it.next();
            if (ankieta.getLektor().getNazwa().toLowerCase().contains(searchPhrase.toLowerCase())
                    || ankieta.getKursant().getNazwa().toLowerCase().contains(searchPhrase.toLowerCase())) {
                resultList.add(ankieta);
            }
        }
        return resultList;
    }

    /**
     *
     * @param mainEntityList
     * @param lektorList
     * @param searchPhrase
     * @return
     */
    public static List findRachunek(List mainEntityList, List lektorList, String searchPhrase) {
        List resultList = new ArrayList<>(); // to send back

//        for every field in entity check whether it contains searchPhrase
//        use toLower to find everything no matter wheter capital      
        Iterator it = mainEntityList.iterator();
        while (it.hasNext()) {
            Rachunek rachunek = (Rachunek) it.next();
            if (rachunek.getNumer().toLowerCase().contains(searchPhrase.toLowerCase())
                    || rachunek.getOpis().toLowerCase().contains(searchPhrase.toLowerCase())
                    || rachunek.getLektor().getNazwa().toLowerCase().contains(searchPhrase.toLowerCase())) {
                resultList.add(rachunek);
            }
        }

        return resultList;

    }

    public static Object findSmallestCourse(List<AbstractEntity> entityList) {
        if (entityList.isEmpty()) {
            return null; // just in case - it will be easier to find any problems
        }
        // LET'S DO IT THIS WAY:
        // create list of entities
        List mainList = new ArrayList();
        for (AbstractEntity abstractEntity : entityList) {
            mainList.add(abstractEntity.getKurs());
        }
        // sort it
        mainList = FieldSorter.sortSymbol(mainList);
        // and get the first one
        return mainList.get(0);
    }

    public static Object findSmallestCustomer(List<AbstractEntity> entityList) {
        if (entityList.isEmpty()) {
            return null; // just in case - it will be easier to find any problems
        }
        // LET'S DO IT THIS WAY:
        // create list of entities
        List mainList = new ArrayList();
        for (AbstractEntity abstractEntity : entityList) {

            // and here we check if not null, meaning if set
            // because if not our dorting will produce null exception
            // this is why we return null when we find one
            if (abstractEntity.getFirma() == null) {
                return null;
            }
            // otherwise do as nothing has happened
            mainList.add(abstractEntity.getFirma());
        }
        // sort it
        mainList = FieldSorter.sortNazwa(mainList);
        // and get the first one
        return mainList.get(0);
    }

    public static Object findSmallestLanguage(List<AbstractEntity> entityList) {
        if (entityList.isEmpty()) {
            return null; // just in case - it will be easier to find any problems
        }
        // LET'S DO IT THIS WAY:
        // create list of entities
        List mainList = new ArrayList();
        for (AbstractEntity abstractEntity : entityList) {
            mainList.add(abstractEntity.getJezyk());
        }
        // sort it
        mainList = FieldSorter.sortNazwa(mainList);
        // and get the first one
        return mainList.get(0);
    }

    public static Object findSmallestLector(List<AbstractEntity> entityList) {
        if (entityList.isEmpty()) {
            return null; // just in case - it will be easier to find any problems
        }
        // LET'S DO IT THIS WAY:
        // create list of entities
        List mainList = new ArrayList();
        for (AbstractEntity abstractEntity : entityList) {

            // and here we check if not null, meaning if set
            // because if not our dorting will produce null exception
            // this is why we return null when we find one
            if (abstractEntity.getLektor() == null) {
                return null;
            }
            // otherwise do as nothing has happened
            mainList.add(abstractEntity.getLektor());
        }
        // sort it
        mainList = FieldSorter.sortNazwa(mainList);
        // and get the first one
        return mainList.get(0);
    }

    public static Object findSmallestParticipant(List<AbstractEntity> entityList) {
        if (entityList.isEmpty()) {
            return null; // just in case - it will be easier to find any problems
        }
        // LET'S DO IT THIS WAY:
        // create list of entities
        List mainList = new ArrayList();
        for (AbstractEntity abstractEntity : entityList) {
            mainList.add(abstractEntity.getKursant());
        }
        // sort it
        mainList = FieldSorter.sortNazwa(mainList);
        // and get the first one
        return mainList.get(0);
    }

    public static List findWplata(List mainEntityList, List firmaList, String searchPhrase) {
        List resultList = new ArrayList<>(); // to send back

//        for every field in entity check whether it contains searchPhrase
//        use toLower to find everything no matter wheter capital      
        Iterator it = mainEntityList.iterator();
        while (it.hasNext()) {
            Wplata wplata = (Wplata) it.next();
            if (wplata.getOpis().toLowerCase().contains(searchPhrase.toLowerCase())
                    || wplata.getFirma().getNazwa().toLowerCase().contains(searchPhrase.toLowerCase())) {
                resultList.add(wplata);
            }
        }

        return resultList;
    }

}
