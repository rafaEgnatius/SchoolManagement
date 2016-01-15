/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helper;

import entity.Kurs;
import entity.KursKursanta;
import entity.Kursant;
import entity.Lekcja;
import entity.Obecnosc;
import finder.SchoolFinder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import session.KursFacade;
import session.LekcjaFacade;
import session.ObecnoscFacade;
import session.persistence.PersistenceManager;
import sorter.EntitySorter;
import sorter.FieldSorter;

/**
 *
 * @author Rafa
 */
@Stateless
public class LessonHelper {

    @EJB
    LekcjaFacade mainEntityFacade;

    @EJB
    KursFacade kursFacade;

    @EJB
    ObecnoscFacade obecnoscFacade;

    @EJB
    PersistenceManager persistanceManager;

    List mainEntityList;
    List kursList;
    List kursantList;
    List firmaList;

    private static final int pageSize = 10; // number of records on one page

    private Boolean sortAsc = true; // and this one to check how to sort
    private String sortBy; // so we know how to sort
    private Boolean changeSort = false; // this one to know whether to change sorting order
    private int numberOfPages; // auxiliary field for calculating number of pages (based on the list size)
    List<List> listOfPages = new ArrayList<List>(); // list of lists of single page records
    private int pageNumber; // current page number

    private String searchPhrase;
    private String searchOption;

    private List filterKurs(List mainEntityList, String kursId) {
        List resultList = new ArrayList();
        Kurs kurs = kursFacade.find(Integer.parseInt(kursId));

        Iterator it = mainEntityList.iterator();
        while (it.hasNext()) {
            Lekcja lekcja = (Lekcja) it.next();
            if (lekcja.getKurs().equals(kurs)) {
                resultList.add(lekcja);
            }
        }
        return resultList;
    }

    // return only these that have proper lesson
    private List filterLesson(List obecnoscList, Lekcja lekcja, Kurs kurs) {
        List resultList = new ArrayList();

        Iterator it = obecnoscList.iterator();
        while (it.hasNext()) {
            Obecnosc obecnosc = (Obecnosc) it.next();
            if (obecnosc.getLekcja().equals(lekcja)) {
                resultList.add(obecnosc);
            }
        }

        // there is a problem when there is a kursant that is member of the kur Collection
        // but not on our resultList
        // now check if anyone is missing
        // we do it by checking elements of the Collection
        it = kurs.getKursKursantaCollection().iterator();

        List testowaLista = (List) kurs.getKursKursantaCollection();

        while (it.hasNext()) {
            KursKursanta kursKursanta = (KursKursanta) it.next();
            Kursant kursant = kursKursanta.getKursant();

            // and now if there is no kursant in our list we just have to acknowledge it
            boolean found = false; // if there are no one on the second list...
            Iterator it2 = resultList.iterator();

            // and check if on the list
            innerloop:
            while (it2.hasNext()) {
                Obecnosc obecnosc = (Obecnosc) it2.next();
                if (obecnosc.getKursant().equals(kursant)) {
                    found = true;
                    break innerloop;
                }
            }
            // and if not found we have to add him
            if (!found) {
                Obecnosc obecnosc = persistanceManager.savePresence(true, lekcja, kursant, false);
                resultList.add(obecnosc);
            }
        }

        return resultList;
    }

    /**
     * Handles preparation of the list
     *
     *
     * @param request
     * @return HttpServletRequest
     */
    public HttpServletRequest prepareEntityList(HttpServletRequest request) {

        mainEntityList = mainEntityFacade.findAll();
        kursList = kursFacade.findAll();

        // we need only those those lessons that have appropiriate course (kurs)
        // in this case we need to filter the results in case we need specific kurs
        String kursId = request.getParameter("kursId");
        if (kursId != null && !kursId.equals("")) {
            mainEntityList = filterKurs(mainEntityList, kursId);
            request.setAttribute("kursId", kursId);
        }

        List resultList;

        try {
            pageNumber = Integer.parseInt(request.getParameter("pageNumber"));
        } catch (NumberFormatException e) {
            pageNumber = 1; // (it seems that we do not have page number yet)
        }

        // SORT & SEARCH
        // check whether to change sorting direction
        changeSort = Boolean.parseBoolean(request.getParameter("changeSort"));
        String stringSortAsc = request.getParameter("sortAsc");
        try {
            sortAsc = Boolean.parseBoolean(stringSortAsc);
        } catch (Exception e) {
            sortAsc = true;
        }

        // check if sorting...
        sortBy = request.getParameter("sortBy");
        // if not sorting let's sort by id
        if (sortBy == null) {
            sortBy = "id";
            sortAsc = true; // to start from the beginning
            changeSort = false;
        }

        // and check if searching
        searchPhrase = request.getParameter("searchPhrase");
        if (searchPhrase != null && !searchPhrase.equals("")) {
            mainEntityList = SchoolFinder.findLanguageTest(mainEntityList, searchPhrase);
        } else {
            searchPhrase = "";
        }

        // and what option was chosen
        searchOption = request.getParameter("searchOption");
        if (searchOption != null && !searchOption.equals("")) {
            mainEntityList = SchoolFinder.findCustomerForLanguageTest(mainEntityList, kursList, searchOption);
        } else {
            searchOption = "";
        }

        // now we check if we have to sort things (out)
        // (by the way - we sort using auxiliary abstract class)
        switch (sortBy) {
            case ("id"):
                if ((sortAsc && changeSort) || (!sortAsc && !changeSort)) {
                    mainEntityList = FieldSorter.sortIdDesc(mainEntityList);
                    sortAsc = false;
                } else {
                    mainEntityList = FieldSorter.sortId(mainEntityList);
                    sortAsc = true;
                }
                break;
            case ("data"):
                if ((sortAsc && changeSort) || (!sortAsc && !changeSort)) {
                    mainEntityList = FieldSorter.sortDataDesc(mainEntityList);
                    sortAsc = false;
                } else {
                    mainEntityList = FieldSorter.sortData(mainEntityList);
                    sortAsc = true;
                }
                break;
            case ("kurs"):
                if ((sortAsc && changeSort) || (!sortAsc && !changeSort)) {
                    mainEntityList = EntitySorter.sortKursDesc(mainEntityList);
                    sortAsc = false;
                } else {
                    mainEntityList = EntitySorter.sortKurs(mainEntityList);
                    sortAsc = true;
                }
                break;
        }

        // PAGINATE
        // and here goes pagination part
        numberOfPages = ((mainEntityList.size()) / pageSize) + 1; // check how many pages

        // pageToDisplay is subList - we check if not get past last index
        int fromIndex = ((pageNumber - 1) * pageSize);
        int toIndex = fromIndex + pageSize;
        resultList = mainEntityList.subList(fromIndex,
                toIndex > mainEntityList.size() ? mainEntityList.size() : toIndex);

        // SEND
        // now prepare things for our JSP
        request.setAttribute("numberOfPages", numberOfPages);
        request.setAttribute("pageNumber", pageNumber);
        request.setAttribute("sortBy", sortBy);
        request.setAttribute("sortAsc", sortAsc);

        request.setAttribute("searchPhrase", searchPhrase);
        request.setAttribute("searchOption", searchOption);

        request.setAttribute("lekcjaList", resultList);
        request.setAttribute("kursList", kursList);

        return request;
    }

    public HttpServletRequest prepareEntityView(HttpServletRequest request, String mainEntityId) {
        // set attributes for main part
        int i = Integer.parseInt(mainEntityId);
        Lekcja lekcja = mainEntityFacade.find(i); // we should try/catch it later
        request.setAttribute("lekcja", lekcja);
        request.setAttribute("kurs", lekcja.getKurs());

        // and now for obecnosc list
        List obecnoscList = obecnoscFacade.findAll();

        // now filter only relevant ones
        obecnoscList = filterLesson(obecnoscList, lekcja, lekcja.getKurs());

        request.setAttribute("obecnoscList", obecnoscList);

        return request;
    }

}