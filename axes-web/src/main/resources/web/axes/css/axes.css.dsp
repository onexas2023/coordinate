<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="zk" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="tld://onexas/axes" prefix="x" %>
${zk:setCSSCacheControl()}
<c:forEach items="${x:cfglist('axes.csss.css')}" var="each">
${x:loadcss(each,x:cfgboolean('axes.csss.[@cache]'),x:cfgboolean('axes.csss.[@compress]'))}
</c:forEach>
.z-loading-icon{
	background-image:url(${c:encodeURL('~./axes/img/processing.gif')});
}
