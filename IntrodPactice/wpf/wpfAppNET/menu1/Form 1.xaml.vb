Imports MySql.Data.MySqlClient
Imports System.Collections.ObjectModel
Imports System.Data
Imports System.Data.SqlClient
Imports wpfAppNET.ConnectionToSQL.Helper

Public Class Form_1
    Dim conString = "server=127.0.0.1:3306;" _
            & "uid=root;" _
            & "pwd=local;" _
            & "database=mydb"
    Dim conn As New MySqlConnection(Me.conString)
    Dim status = False

    Private Sub BtnConn_Click(sender As Object, e As RoutedEventArgs) Handles btnConn.Click
        ' Me.conString = "server=localhost:3306;" _
        '    & "uid=root;" _
        '     & "pwd=root;" _
        '     & "database=myDBconn"
        DBHelper.EstablishConnection()
    End Sub

    Public Property coupons As ObservableCollection(Of layers.Coupon)

    Private Sub BtnSearch_Click(sender As Object, e As RoutedEventArgs) Handles btnSearch.Click
        Dim query = "select distinct ctm.CustomerName as 'CustomerName'
 , od.OrderDate as 'OrderDate'
 , ep.FirstName as 'VenderName'
 from  orders od left join customers ctm
 on od.CustomerID = ctm.CustomerID 
 left join employees ep
 on od.EmployeeID = ep.EmployeeID"
        'load dữ liệu lên lưới
        Me.coupons = New ObservableCollection(Of layers.Coupon)
        Dim dtCoupon = layers.CouponLayer.dataCoupon(query)
        Dim cp As layers.Coupon
        For Each dr As DataRow In dtCoupon.Rows
            cp = New layers.Coupon(dr("CustomerName").ToString(), dr("OrderDate").ToString(), dr("VenderName").ToString())
            Me.coupons.Add(cp)
        Next
        Me.lsvCoupon.ItemsSource = Me.coupons
    End Sub
End Class
