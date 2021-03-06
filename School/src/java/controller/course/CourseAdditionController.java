/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.course;

import entity.Firma;
import entity.Kursant;
import helper.CourseHelper;
import helper.CustomerHelper;
import helper.LectorHelper;
import helper.ParticipantHelper;
import helper.ProgrammeHelper;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import session.FirmaFacade;
import session.JezykFacade;
import session.JezykLektoraFacade;
import session.KursFacade;
import session.KursantFacade;
import session.LektorFacade;
import session.ProgramFacade;
import session.persistence.PersistenceManager;
import validator.FormValidator;

/**
 *
 * @author Rafa
 */
@WebServlet(name = "CourseAdditionController",
        loadOnStartup = 1,
        urlPatterns = {"/dodajFirmeDoKursu",
            "/dodajKursantaDoKursu",
            "/dodajLektoraDoKursu",
            "/dodajProgramDoKursu",
            "/dodajStawkeLektora",
            "/dodajTermin",
            "/usunFirmeZKursu",
            "/usunKursantaZKursu",
            "/usunLektoraZKursu",
            "/usunProgramZKursu",
            "/usunStawkeLektora",
            "/usunTermin",
            "/zapiszDodanieFirmyDoKursu",
            "/zapiszDodanieKursantaDoKursu",
            "/zapiszDodanieLektoraDoKursu",
            "/zapiszDodanieProgramuDoKursu"})
public class CourseAdditionController extends HttpServlet {

    @EJB
    CustomerHelper customerHelper;
    
    @EJB
    LectorHelper lectorHelper;
    
    @EJB
    FirmaFacade firmaFacade;

    @EJB
    JezykFacade jezykFacade;
    
    @EJB
    ParticipantHelper participantHelper;

    @EJB
    KursFacade kursFacade;

    @EJB
    KursantFacade kursantFacade;

    @EJB
    JezykLektoraFacade jezykLektoraFacade;

    @EJB
    LektorFacade lektorFacade;

    @EJB
    ProgramFacade programFacade;

    @EJB
    CourseHelper courseHelper;

    @EJB
    PersistenceManager persistenceManager;
    
    @EJB
    ProgrammeHelper programmeHelper;

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        List kursantList;
        
        String firmaId;
        String kursId;
        String kursantId;
        String lektorId;
        String programId;

        //HttpSession session = request.getSession(); // let's get session - we might need it
        request.setCharacterEncoding("UTF-8"); // for Polish characters
        String userPath = request.getServletPath(); // this way we know where to go

        switch (userPath) {
//  ADD CUSTOMER
            case "/dodajFirmeDoKursu":

                kursId = request.getParameter("kursId"); // it should be set if we are here

                // use helper to get entity list prepared in our request
                request = customerHelper.prepareEntityList(request);

                // tell the kurs id
                request.setAttribute("kursId", kursId);

                // and tell the container where to redirect
                userPath = "/course/course/addCustomer";
                break;

// SAVE CUSTOMER
            case "/zapiszDodanieFirmyDoKursu":
                // it is confirmed so add to database

                // check parameters
                firmaId = request.getParameter("firmaId");
                kursId = request.getParameter("kursId");

                // now persist:
                persistenceManager.saveAddingCustomerToCourseToDatabase(firmaId, kursId);

                // use helper to get lektor list prepared in our request
                request = courseHelper.prepareEntityView(request, kursId);

                // prepare redirect
                userPath = "/course/course/viewOne";
                break;

// REMOVE CUSTOMER
            case "/usunFirmeZKursu":

                // check parameters
                kursId = request.getQueryString();

                // now persist:
                persistenceManager.deleteCustomerFromCourseFromDatabase(kursId);

                // use helper to get lektor list prepared in our request
                request = courseHelper.prepareEntityView(request, kursId);

                // prepare redirect
                userPath = "/course/course/viewOne";
                break;

//  ADD LECTOR
            case "/dodajLektoraDoKursu":

                // use helper to get lektor list prepared in our request
                request = lectorHelper.prepareEntityList(request);

                // tell the kurs id
                request.setAttribute("kursId", request.getParameter("kursId")); // it should be set if we are here

                // and tell the container where to redirect
                userPath = "/course/course/addLector";
                break;

// SAVE LECTOR
            case "/zapiszDodanieLektoraDoKursu":
                // it is confirmed so add to database

                // check parameters
                lektorId = request.getParameter("lektorId");
                kursId = request.getParameter("kursId");

                // now persist:
                persistenceManager.saveAddingLectorToCourseToDatabase(lektorId, kursId);

                // use helper to get lektor list prepared in our request
                request = courseHelper.prepareEntityView(request, kursId);

                // prepare redirect
                userPath = "/course/course/viewOne";
                break;

// REMOVE LECTOR
            case "/usunLektoraZKursu":

                // check parameters
                kursId = request.getQueryString();

                // now persist:
                persistenceManager.deleteLectorFromCourseFromDatabase(kursId);

                // use helper to get lektor list prepared in our request
                request = courseHelper.prepareEntityView(request, kursId);

                // prepare redirect
                userPath = "/course/course/viewOne";
                break;

//  ADD PARTICIPANT
            case "/dodajKursantaDoKursu":

                // check the id
                kursId = request.getParameter("kursId"); // it should be set if we are here

                // we will need this one at least twice
                Firma firma = kursFacade.find(Integer.parseInt(kursId)).getFirma();

                // entity list
                kursantList = new ArrayList();

                // filter only those with proper participants - meaning only those with matching customer (firma)
                Iterator myIterator = kursantFacade.findAll().iterator();
                while (myIterator.hasNext()) {
                    Kursant kursant = (Kursant) myIterator.next();
                    if (kursant.getFirma().equals(firma)) {
                        kursantList.add(kursant);
                    }
                }

                // use helper to get lektor list prepared in our request
                List shortKursantList = (List) firma.getKursantCollection();
                request = participantHelper.prepareEntityList(request, shortKursantList); // only from one customer (firma)

                request.setAttribute("kursId", kursId);

                userPath = "/course/course/addParticipant";
                break;

// SAVE PARTICIPANT
            case "/zapiszDodanieKursantaDoKursu":
                // it is confirmed so add to database
                // check parameters
                kursantId = request.getParameter("kursantId");
                kursId = request.getParameter("kursId");

                // check if not already have this participant
                if (!courseHelper.alreadyThere(Integer.parseInt(kursId), Integer.parseInt(kursantId))) {
                    // persist if not:
                    persistenceManager.saveAddingParticipantToCourseToDatabase(kursFacade.find(Integer.parseInt(kursId)), kursantFacade.find(Integer.parseInt(kursantId)));
                }

                // use helper to get lektor list prepared in our request
                request = courseHelper.prepareEntityView(request, kursId);

                // prepare redirect
                userPath = "/course/course/viewOne";
                break;

// REMOVE PARTICIPANT
            case "/usunKursantaZKursu":

                // check parameters
                kursId = request.getParameter("kursId");
                kursantId = request.getParameter("kursantId");

                // now persist:
                persistenceManager.deleteParticipantFromDatabase(kursId, kursantId);

                // use helper to get lektor list prepared in our request
                request = courseHelper.prepareEntityView(request, kursId);

                // prepare redirect
                userPath = "/course/course/viewOne";
                break;

//  ADD PROGRAMME
            case "/dodajProgramDoKursu":

                // use helper to get lektor list prepared in our request
                request = programmeHelper.prepareEntityList(request);

                // tell the kurs id
                request.setAttribute("kursId", request.getParameter("kursId")); // it should be set if we are here

                userPath = "/course/course/addProgramme";
                break;

// SAVE PROGRAMME
            case "/zapiszDodanieProgramuDoKursu":
                // it is confirmed so add to database

                // check parameters
                programId = request.getParameter("programId");
                kursId = request.getParameter("kursId");

                // now persist:
                persistenceManager.saveAddingProgrammeToCourseToDatabase(programId, kursId);

                // use helper to get lektor list prepared in our request
                request = courseHelper.prepareEntityView(request, kursId);

                // prepare redirect
                userPath = "/course/course/viewOne";
                break;

// REMOVE PROGRAMME
            case "/usunProgramZKursu":

                // check parameters
                kursId = request.getQueryString();

                // now persist:
                persistenceManager.deleteProgrammeCourseFromDatabase(kursId);

                // use helper to get lektor list prepared in our request
                request = courseHelper.prepareEntityView(request, kursId);

                // prepare redirect
                userPath = "/course/course/viewOne";
                break;

// REMOVE RATE (Lector)
            case "/usunStawkeLektora":

                // check parameters
                kursId = request.getParameter("id");
                lektorId = request.getParameter("lektorId");

                // now persist:
                persistenceManager.deleteLectorRateFromDatabase(Integer.parseInt(kursId), Integer.parseInt(lektorId));

                // use helper to get lektor list prepared in our request
                request = courseHelper.prepareEntityView(request, kursId);

                // prepare redirect
                userPath = "/course/course/viewOne";
                break;

// REMOVE DAY AND TIME
            case "/usunTermin":

                // now persist:
                persistenceManager.deleteDayAndTimeFromCourseFromDatabase(request.getParameter("terminId"));

                // use helper to get lektor list prepared in our request
                request = courseHelper.prepareEntityView(request, request.getParameter("kursId"));

                // prepare redirect
                userPath = "/course/course/viewOne";
                break;

// FORWARD
        } // close main swith
        String url = "/WEB-INF/view" + userPath + ".jsp";

        try {
            request.getRequestDispatcher(url).forward(request, response);
        } catch (ServletException | IOException ex) {
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //processRequest(request, response);

        BigDecimal bigDecimalAmount;
        String stawka;
        
        String kursId;
        String lektorId;

        //HttpSession session = request.getSession(); // let's get session - we might need it
        request.setCharacterEncoding("UTF-8"); // for Polish characters
        String userPath = request.getServletPath(); // this way we know where to go

        switch (userPath) {

// ADD DAY AND TIME
            case "/dodajTermin":
                // it is confirmed so add to database

                // check parameters
                kursId = request.getParameter("id");
                String stringGodzinaStart = request.getParameter("godzinaStart");
                String stringGodzinaStop = request.getParameter("godzinaStop");

                // validate
                boolean formError = false;
                Calendar godzinaStart;
                Calendar godzinaStop;

                if ((godzinaStart = FormValidator.validateTime(stringGodzinaStart)) == null) {
                    request.setAttribute("godzinaStartError", "błędne dane");
                    formError = true;
                } else {
                    request.setAttribute("godzinaStart", stringGodzinaStart);
                }
                if ((godzinaStop = FormValidator.validateTime(stringGodzinaStop)) == null) {
                    request.setAttribute("godzinaStopError", "błędne dane");
                    formError = true;
                } else {
                    request.setAttribute("godzinaStop", stringGodzinaStop);
                }

                if (!formError) {
                    // now persist:
                    persistenceManager.saveAddingDayAndTimeToCourseToDatabase(kursId, request.getParameter("dzien"), godzinaStart, godzinaStop);
                    request.removeAttribute("godzinaStart");
                    request.removeAttribute("godzinaStop");
                }

                // use helper to get lektor list prepared in our request
                request = courseHelper.prepareEntityView(request, kursId);

                // prepare redirect
                userPath = "/course/course/viewOne";
                break;

//  ADD RATE (lector)
            case "/dodajStawkeLektora":

                // get Parameters
                kursId = request.getParameter("id"); // it should be set if we are here
                lektorId = request.getParameter("lektorId");
                stawka = request.getParameter("stawka");

                if ((bigDecimalAmount = FormValidator.validateMoney(stawka)) == null) {
                    request.setAttribute("stawkaError", "błędne dane");
                } else {
                    persistenceManager.saveLectorRateToDatabase(Integer.parseInt(kursId), Integer.parseInt(lektorId), bigDecimalAmount);
                }

                // use helper to get lektor list prepared in our request
                request = courseHelper.prepareEntityView(request, kursId);

                // prepare redirect
                userPath = "/course/course/viewOne";
                break;

// FORWARD
        } // close main swith
        String url = "/WEB-INF/view" + userPath + ".jsp";

        try {
            request.getRequestDispatcher(url).forward(request, response);
        } catch (ServletException | IOException ex) {
        }

    }
}
