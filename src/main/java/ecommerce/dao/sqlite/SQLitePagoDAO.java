package ecommerce.dao.sqlite;

import ecommerce.dao.interfaces.PagoDAO;
import ecommerce.enums.EstadoPago;
import ecommerce.enums.MetodoPago;
import ecommerce.exception.DatabaseException;
import ecommerce.model.Pago;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLitePagoDAO extends SQLiteBaseDAO implements PagoDAO {

    @Override
    public void guardar(Pago pago) {
        String sql = """
                INSERT INTO pagos (metodo_pago, monto, estado, fecha)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = prepararConGeneratedKeys(connection, sql,
                     pago.getMetodoPago(),
                     pago.getMonto(),
                     pago.getEstado(),
                     pago.getFecha())) {

            statement.executeUpdate();
            pago.setId(obtenerIdGenerado(statement));
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudo guardar el pago.", ex);
        }
    }

    @Override
    public Pago buscarPorId(int id) {
        String sql = "SELECT * FROM pagos WHERE id = ?";

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql, id);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return mapearPago(resultSet);
            }
            throw new DatabaseException("No se encontró un pago con ID " + id + ".");
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudo buscar el pago por ID.", ex);
        }
    }

    @Override
    public List<Pago> obtenerTodos() {
        String sql = "SELECT * FROM pagos ORDER BY id";
        List<Pago> pagos = new ArrayList<>();

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                pagos.add(mapearPago(resultSet));
            }
            return pagos;
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudieron obtener los pagos.", ex);
        }
    }

    @Override
    public void actualizar(Pago pago) {
        String sql = "UPDATE pagos SET metodo_pago = ?, monto = ?, estado = ?, fecha = ? WHERE id = ?";

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql,
                     pago.getMetodoPago(),
                     pago.getMonto(),
                     pago.getEstado(),
                     pago.getFecha(),
                     pago.getId())) {

            validarFilasAfectadas(statement.executeUpdate(), "No se encontró el pago a actualizar.");
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudo actualizar el pago.", ex);
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM pagos WHERE id = ?";

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = preparar(connection, sql, id)) {

            validarFilasAfectadas(statement.executeUpdate(), "No se encontró el pago a eliminar.");
        } catch (SQLException ex) {
            throw new DatabaseException("No se pudo eliminar el pago.", ex);
        }
    }

    Pago mapearPago(ResultSet resultSet) throws SQLException {
        return new Pago(
                resultSet.getInt("id"),
                MetodoPago.valueOf(resultSet.getString("metodo_pago")),
                resultSet.getDouble("monto"),
                EstadoPago.valueOf(resultSet.getString("estado")),
                leerLocalDateTime(resultSet, "fecha"));
    }
}
