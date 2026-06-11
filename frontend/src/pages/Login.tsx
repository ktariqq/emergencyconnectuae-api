import {useState} from 'react';
import {useNavigate} from 'react-router-dom';
import api from '../api/axios';
import styles from './Auth.module.css';

export default function Login() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [otp, setOtp] = useState('');
    const [otpRequired, setOtpRequired] = useState(false);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleLogin = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');
        try {
            const res = await api.post('/auth/login', {username, password});
            if (res.data === 'OTP_REQUIRED') {
                setOtpRequired(true);
            } else {
                localStorage.setItem('token', res.data);
                navigate('/dashboard');
            }
        } catch {
            setError('Invalid credentials.');
        }
    };

    const handleOtp = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            const res = await api.post('/auth/verify-otp', {username, otp});
            localStorage.setItem('token', res.data);
            navigate('/dashboard');
        } catch {
            setError('Invalid or expired OTP.');
        }
    };

    return (
        <div className={styles.page}>
            <div className={styles.card}>
                <h1>🚨 EmergencyConnect<span>UAE</span></h1>
                <p className={styles.sub}>Secure Emergency Coordination Platform</p>

                {!otpRequired ? (
                    <form onSubmit={handleLogin} className={styles.form}>
                        <input placeholder="Username" value={username}
                               onChange={e => setUsername(e.target.value)} required/>
                        <input type="password" placeholder="Password" value={password}
                               onChange={e => setPassword(e.target.value)} required/>
                        {error && <p className={styles.error}>{error}</p>}
                        <button type="submit">Login</button>
                    </form>
                ) : (
                    <form onSubmit={handleOtp} className={styles.form}>
                        <p className={styles.otpNote}>
                            An OTP has been sent to your registered contact. Enter it below.
                        </p>
                        <input placeholder="6-digit OTP" value={otp}
                               onChange={e => setOtp(e.target.value)} required maxLength={6}/>
                        {error && <p className={styles.error}>{error}</p>}
                        <button type="submit">Verify OTP</button>
                    </form>
                )}
            </div>
        </div>
    );
}