Imports System.Data
Imports MySql.Data.MySqlClient
Imports wpfAppNET.ConnectionToSQL.Helper

Namespace layers
    Public Class Category

        Dim _categoryID As String
        Dim _categoryName As String
        Dim _description As String
        Private Property CategoryID() As String
            Get
                Return Me._categoryID
            End Get
            Set(value As String)
                Me._categoryID = value
            End Set
        End Property
        Private Property CategoryName() As String
            Get
                Return Me._categoryName
            End Get
            Set(value As String)
                Me._categoryName = value
            End Set
        End Property
        Private Property Description() As String
            Get
                Return Me._description
            End Get
            Set(value As String)
                Me._description = value
            End Set
        End Property

        Public Sub New(categoryID As String, categoryName As String, description As String)
            Me._categoryID = categoryID
            Me._categoryName = categoryName
            Me._description = description
        End Sub
        Public Sub New(categoryName As String, description As String)
            Me.CategoryID = 0
            Me.categoryName = categoryName
            Me.description = description
        End Sub

        Public Overrides Function ToString() As String
            Dim result = "CategoryID: " + Me.CategoryID
            result += vbNewLine + "CategoryName: " + Me.CategoryName
            result += vbNewLine + "CategoryDescription: " + Me.Description
            Return result
        End Function

        Public Function getID() As Integer
            Return Me.CategoryID
        End Function
        Public Sub setID(ByVal categoryID As Integer)
            Me.CategoryID = categoryID
        End Sub
    End Class

    Public Class Coupon
        Dim _CustomerName As String
        Dim _OrderDate As String
        Dim _VenderName As String
        Public Sub New()
        End Sub
        Public Sub New(ByVal name As String, ByVal orderDate As String, ByVal venderName As String)
            Me._CustomerName = name
            Me._OrderDate = orderDate
            Me._VenderName = venderName
        End Sub
        Public Property CustomerName
            Get
                Return Me._CustomerName
            End Get
            Set(value)
                Me._CustomerName = value
            End Set
        End Property
        Public Property OrderDate
            Get
                Return Me._OrderDate
            End Get
            Set(value)
                Me._OrderDate = value
            End Set
        End Property
        Public Property VenderName
            Get
                Return Me._VenderName
            End Get
            Set(value)
                Me._VenderName = value
            End Set
        End Property

    End Class
    Public Class CategoriesLayer
        Shared cmd As MySqlCommand
        Shared dataTable As DataTable
        Shared sda As MySqlDataAdapter
        Public Shared Function retrieveCategories(ByVal categoryID As String) As Category
            Dim query = "select CategoryID, CategoryName , Description from categories where CategoryID = "
            query += categoryID
            Dim category As Category
            category = Nothing
            cmd = DBHelper.RunQuery(query, categoryID)
            If (cmd IsNot Nothing) Then
                dataTable = New DataTable()
                sda = New MySqlDataAdapter(cmd)
                sda.Fill(dataTable)
                Dim dtRow = dataTable.Rows
                category = New Category(dtRow(0)("CategoryID").ToString(), dtRow(0)("CategoryName").ToString(), dtRow(0)("Description").ToString())

            End If
            Return category
        End Function

        Public Shared Function getCategory1() As Category
            Dim query = "select CategoryName , Description from categories where CategoryID = 1 "
            Dim category As Category
            category = Nothing
            cmd = DBHelper.queryRun(query)
            If (cmd IsNot Nothing) Then
                dataTable = New DataTable()
                sda = New MySqlDataAdapter(cmd)
                sda.Fill(dataTable)
                Dim dtRow = dataTable.Rows
                category = New Category(dtRow(0)("CategoryName").ToString(), dtRow(0)("Description").ToString())
            End If
            Return category
        End Function

    End Class

    Public Class CouponLayer
        Shared cmd As MySqlCommand
        Shared dataTable As DataTable
        Shared sda As MySqlDataAdapter

        Public Shared Function dataCoupon(ByVal query As String) As DataTable
            Dim dt = New DataTable()
            cmd = DBHelper.queryRun(query)
            Dim listCoupon As DataTable
            listCoupon = Nothing
            If (cmd IsNot Nothing) Then
                sda = New MySqlDataAdapter(cmd)
                sda.Fill(dt)
                listCoupon = New DataTable()
                listCoupon.Columns.Add(New DataColumn With {
                    .ColumnName = "CustomerName",
                    .DataType = GetType(String)})
                listCoupon.Columns.Add(New DataColumn With {
                    .ColumnName = "OrderDate",
                    .DataType = GetType(String)})
                listCoupon.Columns.Add(New DataColumn With {
                    .ColumnName = "VenderName",
                    .DataType = GetType(String)})
                For Each dr As DataRow In dt.Rows
                    listCoupon.Rows.Add(dr("CustomerName"), dr("OrderDate"), dr("VenderName"))
                Next
            End If
            Return listCoupon
        End Function

    End Class

End Namespace
