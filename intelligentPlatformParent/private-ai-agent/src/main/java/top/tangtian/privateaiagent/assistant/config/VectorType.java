package top.tangtian.privateaiagent.assistant.config;

import com.pgvector.PGvector;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @program: ai-platform
 * @description: VectorType
 * @author: tangtian
 * @create: 2026-02-12 14:25
 **/
public class VectorType implements UserType<PGvector> {

    @Override
    public int getSqlType() {
        return Types.OTHER;
    }

    @Override
    public Class<PGvector> returnedClass() {
        return PGvector.class;
    }

    @Override
    public boolean equals(PGvector x, PGvector y) {
        return x == null ? y == null : x.equals(y);
    }

    @Override
    public int hashCode(PGvector x) {
        return x == null ? 0 : x.hashCode();
    }

    @Override
    public PGvector nullSafeGet(ResultSet rs, int position,
                                SharedSessionContractImplementor session,
                                Object owner) throws SQLException {
        String value = rs.getString(position);
        return value == null ? null : new PGvector(value);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, PGvector value,
                            int index,
                            SharedSessionContractImplementor session) throws SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
        } else {
            st.setObject(index, value);
        }
    }

    @Override
    public PGvector deepCopy(PGvector value) {
        try {
            return value == null ? null : new PGvector(value.toString());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(PGvector value) {
        return value == null ? null : value.toString();
    }

    @Override
    public PGvector assemble(Serializable cached, Object owner) {
        try {
            return cached == null ? null : new PGvector((String) cached);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PGvector replace(PGvector detached, PGvector managed, Object owner) {
        return deepCopy(detached);
    }
}