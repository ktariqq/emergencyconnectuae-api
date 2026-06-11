import {Link, useNavigate} from 'react-router-dom';
import styles from './Navbar.module.css';

export default function Navbar() {
    const navigate = useNavigate();
    const token = localStorage.getItem('token');

    const logout = () => {
        localStorage.removeItem('token');
        navigate('/login');
    };

    return (
        <nav className={styles.nav}>
            <span className={styles.brand}>🚨 EmergencyConnect<span>UAE</span></span>
            <div className={styles.links}>
                {token ? (
                    <>
                        <Link to="/dashboard">Dashboard</Link>
                        <Link to="/incidents">Incidents</Link>
                        <Link to="/units">Units</Link>
                        <Link to="/assignments">Assignments</Link>
                        <button onClick={logout} className={styles.logout}>Logout</button>
                    </>
                ) : (
                    <Link to="/login">Login</Link>
                )}
            </div>
        </nav>
    );
}