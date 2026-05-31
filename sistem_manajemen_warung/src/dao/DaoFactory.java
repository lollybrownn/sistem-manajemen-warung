package dao;

import dao.impl.ProdukDaoImpl;
import dao.impl.TransaksiDaoImpl;
import dao.impl.UserDaoImpl;

public class DaoFactory {
    public static IUserDao     getUserDao()     { return UserDaoImpl.getInstance(); }
    public static IProdukDao   getProdukDao()   { return ProdukDaoImpl.getInstance(); }
    public static ITransaksiDao getTransaksiDao(){ return TransaksiDaoImpl.getInstance(); }
}
