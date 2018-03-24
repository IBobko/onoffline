<%--@elvariable id="friendsActive" type="java.lang.String"--%>
<%--@elvariable id="outActive" type="java.lang.String"--%>
<%--@elvariable id="inActive" type="java.lang.String"--%>
<%--@elvariable id="searchActive" type="java.lang.String"--%>
<%--@elvariable id="friendData" type="ru.todo100.activer.data.FriendsData"--%>
<%--@elvariable id="staticFiles" type="java.lang.String"--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<style type="text/css">
    .manBlock {
        margin: 20px 0;
    }

    .searchBlock{
        margin: 20px 0;
    }

    .advancedSearch{
        font-weight: normal;
    }

    #advancedSearch td {
        padding: 10px;;
    }
</style>

<div>
    <ul class="nav nav-tabs" role="tablist">
        <li role="presentation" ${friendsActive}><a onclick="tabClick('')" href="#Friends" aria-controls="friends" role="tab" data-toggle="tab">Друзья</a></li>
        <li role="presentation" ${outActive}><a href="#outFriends" onclick="tabClick('out')"  aria-controls="outFriends" role="tab" data-toggle="tab">Входящие заявки в друзья</a></li>
        <li role="presentation" ${inActive}><a href="#inFriends" onclick="tabClick('in')" aria-controls="inFriends" role="tab" data-toggle="tab">Исходящие заявки в друзья</a></li>
        <li role="presentation" ${searchActive}><a href="#searchFriends" onclick="tabClick('search')" aria-controls="searchFriends" role="tab" data-toggle="tab">Поиск друзей</a></li>
    </ul>

    <div class="tab-content">
        <div role="tabpanel" class="tab-pane active" id="Friends" >
            <c:forEach items="${friendData.friends}" var="friend">
                <div class="manBlock">
                    <img src="${staticImages}/${friend.photo60x60}." width="80" style="float:left">
                    <div style="margin: 0 100px">
                        <a href="<c:url value="/profile/id${friend.id}"/>">${friend.firstName}&nbsp;${friend.lastName}</a><br/>
                        <span style="font-weight: normal">${friend.job.post}&nbsp;-&nbsp;${friend.job.work}</span><br/>
                        <a class="std-button btn btn-default" style="padding:5px 10px;margin-top:16px;" href="<c:url value="/message?dialog=${friend.id}"/>"><span class="fa fa-envelope"></span>Сообщение</a>
                    </div>
                </div>
            </c:forEach>
        </div>
        <div role="tabpanel" class="tab-pane" id="outFriends" onclick="tabClick('out')">
            <c:forEach items="${friendData.outRequest}" var="friend">
                <div class="manBlock">
                    <img src="${staticImages}/${friend.photo60x60}." width="80" style="float:left">
                    <div style="margin: 0 100px">
                        <a href="<c:url value="/profile/id${friend.id}"/>">${friend.firstName}&nbsp;${friend.lastName}</a><br/>
                        <span style="font-weight: normal">${friend.job.post} ${friend.job.work}</span><br/>
                        <a class="std-button btn btn-default" style="padding:5px 10px;margin-top:16px;" href="<c:url value="/message?dialog=${friend.id}"/>"><span class="fa fa-envelope"></span>Сообщение</a><br/>
                        <a style="font-weight: normal" href="<c:url value="/friend/add/${friend.id}"/>">Добавить в друзья</a>
                    </div>
                </div>
            </c:forEach>
        </div>
        <div role="tabpanel" class="tab-pane" id="inFriends" onclick="tabClick('in')">
            <c:forEach items="${friendData.inRequest}" var="friend">
                <div class="manBlock">
                    <img src="${staticImages}/${friend.photo60x60}." width="80" style="float:left">
                    <div style="margin: 0 100px">
                        <a href="<c:url value="/profile/id${friend.id}"/>">${friend.firstName}&nbsp;${friend.lastName}</a><br/>
                        <span style="font-weight: normal">${friend.job.post} ${friend.job.work}</span><br/>
                        <a class="std-button btn btn-default" style="padding:5px 10px;margin-top:16px;" href="<c:url value="/message?dialog=${friend.id}"/>"><span class="fa fa-envelope"></span>Сообщение</a><br/>
                        <a style="font-weight: normal" href="<c:url value="/friend/delete/${friend.id}"/>">Отменить заявку</a><br/>
                    </div>
                </div>
            </c:forEach>

        </div>

        <div role="tabpanel" class="tab-pane" id="searchFriends">
            <div class="searchBlock">
                <c:url var="action" value="/friend/search"/>
            <form:form modelAttribute="friendSearchForm" method="post" action="${action}">
                <div class="advancedSearch" style="text-align:center;">
                    <table id="advancedSearch">
                        <tr>
                            <td>E-mail</td>
                            <td><form:input path="email" cssClass="form-control" /></td>
                        </tr>
                        <tr>
                            <td>Пол</td>
                            <td><form:input path="sex" cssClass="form-control"/></td>
                        </tr>
                        <tr>
                            <td>Имя</td>
                            <td><form:input path="firstName" cssClass="form-control"/></td>
                        </tr>
                        <tr>
                            <td>Фамилия</td>
                            <td><form:input path="lastName" cssClass="form-control"/></td>
                        </tr>
                        <tr>
                            <td>Страна</td>
                            <td><form:input path="countryCode" cssClass="form-control"/></td>
                        </tr>
                        <tr>
                            <td>Город</td>
                            <td><form:input path="city" cssClass="form-control"/></td>
                        </tr>
                        <tr>
                            <td>Дата рождения</td>
                            <td><div style="width:202px" class="input-group date form_date col-md-5" data-date=""
                                                                     data-date-format="dd/mm/yyyy" data-link-format="yyyy-mm-dd">
                                <form:input type="text" path="birthDay" cssClass="form-control"
                                            cssStyle="background-color: white" readonly="true"/>
                                <span class="input-group-addon"><span
                                        class="glyphicon glyphicon-remove"></span></span>
                            <span class="input-group-addon"><span
                                    class="glyphicon glyphicon-calendar"></span></span>
                            </div>
                            </td>
                        </tr>
                    </table>
                    <button type="submit" class="std-button btn btn-default"><span class="glyphicon glyphicon-search"></span>&nbsp;Искать</button>
                </div>
            </form:form>
            </div>

            <c:forEach items="${searchResult}" var="friend">
                <div class="manBlock" style="width:400px">
                    <img src="${staticImages}/${friend.photo60x60}." width="80" style="float:left">
                    <div style="margin: 0 100px">
                        <a href="<c:url value="/profile/id${friend.id}"/>">${friend.firstName}&nbsp;${friend.lastName}</a><br/>
                        <a style="font-weight: normal" href="<c:url value="/message/${friend.id}"/>">Написать сообщение</a><br/>
                        <a style="font-weight: normal" href="<c:url value="/friend/add/${friend.id}"/>">Добавить в друзья</a><br/>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>
</div>

<script type="text/javascript">
    $('.nav-tabs a[href="#${listType}Friends"]').tab('show');

    function tabClick(tab) {
        window.history.pushState('page2', 'Title', '<c:url value="/friend/"/>' + tab);
    }

</script>