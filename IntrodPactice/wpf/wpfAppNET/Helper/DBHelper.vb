Imports System.Data
Imports MySql.Data.MySqlClient

Namespace ConnectionToSQL.Helper

    Public Class DBHelper
        Shared connection As MySqlConnection
        Shared cmd As MySqlCommand
        Shared dt As DataTable
        Shared sdt As MySqlDataAdapter

        Public Shared Sub EstablishConnection()
            Try
                Dim builder = New MySqlConnectionStringBuilder()
                builder.Server = "127.0.0.1"
                builder.UserID = "root"
                builder.Password = "local"
                builder.Database = "mydb"
                builder.SslMode = MySqlSslMode.None
                DBHelper.connection = New MySqlConnection(builder.ToString())
                MessageBox.Show("Kết nối đc gồi")
            Catch ex As Exception
                MessageBox.Show("Méo kết nối đc !")
            End Try
        End Sub

        'run chỉ với 1 para / 1 query
        Public Shared Function RunQuery(ByVal query As String, ByVal para As String) As MySqlCommand
            Try
                If (IsNothing(DBHelper.connection)) Then
                Else
                    DBHelper.connection.Open()
                    cmd = DBHelper.connection.CreateCommand()
                    cmd.CommandType = CommandType.Text
                    cmd.CommandText = query
                    cmd.Parameters.AddWithValue("@id", para)
                    cmd.ExecuteNonQuery()
                    'cmd.Parameters.AddWithValue("@id", para)
                    DBHelper.connection.Close()
                End If
            Catch ex As Exception
                connection.Close()
            End Try
            Return cmd
        End Function
        '
        Public Shared Function queryRun(query As String) As MySqlCommand
            Try
                If (IsNothing(DBHelper.connection)) Then
                Else
                    DBHelper.connection.Open()
                    cmd = DBHelper.connection.CreateCommand()
                    cmd.CommandType = CommandType.Text
                    cmd.CommandText = query
                    cmd.ExecuteNonQuery()
                    DBHelper.connection.Close()
                End If
            Catch ex As Exception
                connection.Close()
            End Try
            Return cmd
        End Function
    End Class

End Namespace