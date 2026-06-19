-- Gastos e ingresos para usuario1 (id=2)
INSERT INTO transactions (amount, description, category, date, user_id, type, applicated_at_balance, created_at, version)
VALUES
-- Gastos de junio
(45.50, 'Comida semanal', 'Food', '2026-06-01', 2, 'EXPENSE', false, NOW(), 0),
(120.00, 'Recibo de luz', 'Utilities', '2026-06-03', 2, 'EXPENSE', false, NOW(), 0),
(35.00, 'Gasolina', 'Transport', '2026-06-05', 2, 'EXPENSE', false, NOW(), 0),
(15.99, 'Netflix mensual', 'Entertainment', '2026-06-07', 2, 'EXPENSE', false, NOW(), 0),
(250.00, 'Alquiler junio', 'Rent', '2026-06-01', 2, 'EXPENSE', false, NOW(), 0),
(8.50, 'Café con amigos', 'Food', '2026-06-10', 2, 'EXPENSE', false, NOW(), 0),
(60.00, 'Seguro coche', 'Insurance', '2026-06-12', 2, 'EXPENSE', false, NOW(), 0),
(22.30, 'Cena restaurante', 'Food', '2026-06-14', 2, 'EXPENSE', false, NOW(), 0),

-- Ingresos de junio
(2800.00, 'Nómina junio', 'Salary', '2026-06-01', 2, 'INCOME', false, NOW(), 0),
(150.00, 'Freelance diseño web', 'Freelance', '2026-06-08', 2, 'INCOME', false, NOW(), 0),
(45.00, 'Venta libros usados', 'Extra', '2026-06-15', 2, 'INCOME', false, NOW(), 0),

-- Gastos de mayo (ya aplicados al balance)
(42.00, 'Comida semanal', 'Food', '2026-05-04', 2, 'EXPENSE', true, NOW(), 0),
(110.00, 'Recibo de luz', 'Utilities', '2026-05-05', 2, 'EXPENSE', true, NOW(), 0),
(2800.00, 'Nómina mayo', 'Salary', '2026-05-01', 2, 'INCOME', true, NOW(), 0),
(200.00, 'Freelance proyecto', 'Freelance', '2026-05-15', 2, 'INCOME', true, NOW(), 0),

-- Gastos varios abril (aplicados)
(38.00, 'Gasolina', 'Transport', '2026-04-10', 2, 'EXPENSE', true, NOW(), 0),
(250.00, 'Alquiler abril', 'Rent', '2026-04-01', 2, 'EXPENSE', true, NOW(), 0),
(2800.00, 'Nómina abril', 'Salary', '2026-04-01', 2, 'INCOME', true, NOW(), 0);
