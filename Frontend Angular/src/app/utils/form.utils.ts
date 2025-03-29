export function limitInputLength(event: any, maxLength: number): void {
  let input = event.target.value;

  if (input.length > maxLength) {
    event.target.value = input.slice(0, maxLength);
  }
}
