# TODO - UI fixes & ticket total calculation

## Step 1: PaymentPage total calculation (UI only)
- Update `PaymentPage` to display:
  - Movie Name
  - Seats Selected
  - Price Per Seat (₹150)
  - Total Amount = totalSeats * 150
- Compute automatically on page open (no extra user input).
- Keep existing pay flow and DB insert logic unchanged.

## Step 2: Responsiveness improvements
- Fix the PaymentPage field styling wrapper issue (so the styled background/padding is actually applied).
- Ensure the layout uses existing GridBagLayout constraints without component overlap.

## Step 3: English text + font consistency
- Ensure all labels/messages remain proper English.
- Apply consistent font family (Segoe UI / SansSerif) across PaymentPage at minimum.

## Step 4: Scan for broken text rendering
- Search all `.java` for any non-ASCII / corrupted literals.
- Replace only the corrupted UI strings with correct English.

## Step 5: Compile & quick run check
- Compile the project and ensure no compile/runtime errors.

