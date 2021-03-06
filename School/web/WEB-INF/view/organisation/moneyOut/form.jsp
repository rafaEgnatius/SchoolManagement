<%-- 
    Document   : MONEYOUT add
    Author     : Rafa
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<section id="section-szkola-one-column">

    <form action="dodajWyplatePotwierdz" method="POST">
        <table class="formTable">
            <tr>
                <th colspan="2">wprowadź informacje dotyczące wypłaty:</th>
                <td colspan="2" class="formError">${formError eq "formError" ? "Formularz zawiera błędy:" : ""}</td>
            </tr>
            <tr>
                <td><label for="data">Data (rrrr-mm-dd): </label></td>
                <td><input type="date" name="data" value="${data}" /></td>
                <td class="formErrorAsterix">${dataError}</td>
            </tr>
            <tr>
                <td><label for="kwota">Kwota: </label></td>
                <td><input type="number" name="kwota" min="0" max="9999999" step="0.01" value="${kwota}" /></td>
<!--                <td><input type="text" name="kwota" value="${kwota}" /></td>-->
                <td class="formErrorAsterix">${kwotaError}</td>
            </tr>
            <tr>
                <td><label for="opis">Opis: </label></td>
                <td><textarea name="opis" rows="3" class="inputForm">${opis}</textarea></td>
                <td class="formErrorAsterix">${opisError}</td>
            </tr>
            <tr>
                <td><label for="lektorId">Lektor: </label></td>
                <td>
                    <select name="lektorId">
                        <c:forEach var="lektorToSelect" items="${lektorList}">
                            <option value="${lektorToSelect.id}" ${lektorToSelect eq lektor ? "selected" : ""}>${lektorToSelect.nazwa}</option>
                        </c:forEach>
                    </select>
                </td>
                
                <td class="formErrorAsterix">${lektorError}</td>
            </tr>
            <tr>
                <td colspan="2"><input type="submit" value="Zapisz" /></td>
                <td></td>
            </tr>
        </table>
        <input type="hidden" name="id" value="${id}" />
        <!-- we use this one in case we are editing -->
    </form>

</section>