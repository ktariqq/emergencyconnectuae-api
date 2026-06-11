import {useEffect, useState} from 'react';
import api from '../api/axios';
import styles from './Assignments.module.css';

export default function Assignments() {
    const [assignments, setAssignments] = useState([]);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [incidents, setIncidents] = useState([]);
    const [units, setUnits] = useState([]);
    const [incidentId, setIncidentId] = useState('');
    const [unitId, setUnitId] = useState('');
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const fetchAll = () => {
        api.get(`/assignments?page=${page}&size=10`).then(r => {
            setAssignments(r.data.content);
            setTotalPages(r.data.totalPages);
        });
    };

    useEffect(() => {
        fetchAll();
    }, [page]);

    useEffect(() => {
        api.get('/incidents/active').then(r => setIncidents(r.data));
        api.get('/units/available').then(r => setUnits(r.data));
    }, []);

    const handleAssign = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');
        setSuccess('');
        try {
            await api.post(`/assignments?incidentId=${incidentId}&unitId=${unitId}`);
            setSuccess('Unit assigned successfully.');
            setIncidentId('');
            setUnitId('');
            fetchAll();
            // Refresh dropdowns
            api.get('/incidents/active').then(r => setIncidents(r.data));
            api.get('/units/available').then(r => setUnits(r.data));
        } catch (err: any) {
            setError(err.response?.data?.error || 'Assignment failed.');
        }
    };

    return (
        <div className={styles.page}>
            <h2 className={styles.title}>Assignments</h2>

            <form onSubmit={handleAssign} className={styles.form}>
                <h3>Assign Unit to Incident</h3>
                <p className={styles.note}>
                    Protected by Redis distributed lock — no two dispatchers can claim the same unit simultaneously.
                </p>
                <div className={styles.formRow}>
                    <select value={incidentId} onChange={e => setIncidentId(e.target.value)} required>
                        <option value="">Select Active Incident</option>
                        {(incidents as any[]).map((i: any) => (
                            <option key={i.id} value={i.id}>
                                [{i.severity}] {i.title} — {i.region}
                            </option>
                        ))}
                    </select>
                    <select value={unitId} onChange={e => setUnitId(e.target.value)} required>
                        <option value="">Select Available Unit</option>
                        {(units as any[]).map((u: any) => (
                            <option key={u.id} value={u.id}>
                                {u.name} ({u.type}) — {u.region}
                            </option>
                        ))}
                    </select>
                </div>
                {error && <p className={styles.error}>{error}</p>}
                {success && <p className={styles.success}>{success}</p>}
                <button type="submit">Assign Unit</button>
            </form>

            <div className={styles.list}>
                <h3 className={styles.subTitle}>Assignment History</h3>
                {(assignments as any[]).map((a: any) => (
                    <div key={a.id} className={styles.card}>
                        <div className={styles.cardRow}>
                            <div>
                                <span className={styles.label}>Incident</span>
                                <span className={styles.value}>{a.incident?.title}</span>
                            </div>
                            <div>
                                <span className={styles.label}>Unit</span>
                                <span className={styles.value}>{a.unit?.name}</span>
                            </div>
                            <div>
                                <span className={styles.label}>Assigned By</span>
                                <span className={styles.value}>{a.assignedBy}</span>
                            </div>
                            <div>
                                <span className={styles.label}>Time</span>
                                <span className={styles.value}>
                  {new Date(a.assignedAt).toLocaleString()}
                </span>
                            </div>
                        </div>
                    </div>
                ))}
            </div>

            <div className={styles.pagination}>
                <button disabled={page === 0} onClick={() => setPage(p => p - 1)}>←</button>
                <span>Page {page + 1} of {totalPages}</span>
                <button disabled={page >= totalPages - 1} onClick={() => setPage(p => p + 1)}>→</button>
            </div>
        </div>
    );
}