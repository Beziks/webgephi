<!DOCTYPE html>
<% String contextPath = session.getServletContext().getContextPath(); %>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Webgephi Server</title>
    <meta name="description" content="">
    <meta name="author" content="">

    <!-- Le HTML5 shim, for IE6-8 support of HTML elements -->
    <!--[if lt IE 9]>
    <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->

    <!-- Le styles -->
    <link href="<%= contextPath %>/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="<%= contextPath %>/bootstrap/css/bootstrap-theme.min.css" rel="stylesheet">
    <link href="<%= contextPath %>/css/application.css" rel="stylesheet">
    <link href="<%= contextPath %>/highlight/default.css" rel="stylesheet">

    <!-- Javascript -->
    <script src="https://code.jquery.com/jquery-1.10.2.min.js"></script>
    <script src="<%= contextPath %>/bootstrap/js/bootstrap.js"></script>

    <script src="<%= contextPath %>/highlight/highlight.pack.js"></script>

    <!--
    <script type="text/javascript">
        erraiBusRemoteCommunicationEnabled = false;
    </script>
    -->

    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
    <script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
    <![endif]-->

    <!-- Le fav and touch icons -->
    <link rel="shortcut icon" href="<%= contextPath %>/favicon.ico">

    <script type="text/javascript" language="javascript" src="<%= contextPath %>/app/app.nocache.js"></script>
</head>

<body>

<div id="rootPanel"></div>

<iframe src="javascript:''" id="__gwt_historyFrame" style="width: 0; height: 0; border: 0"></iframe>

</body>
</html>
